package com.example.bkpaymenttest.client.bpay;

import com.example.bkpaymenttest.common.crypto.CryptoUtils;
import com.example.bkpaymenttest.config.CryptoProperties;
import com.example.bkpaymenttest.config.EnvConfig;
import com.example.bkpaymenttest.dto.common.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class BpayClient {

    private static final String APP_CD = "B_JBK";
    private static final String ORG_ID = "PBP2511000011";
    private static final String REQ_ORIGIN = "J";

    private final CryptoProperties cryptoProperties;
    private final EnvConfig envConfig;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    /**
     * 비플페이 API 공통 호출
     *
     * 흐름: COMM + DETAIL 생성 → JSON 직렬화 → AES-GCM 암호화 → HTTP POST
     *       → 응답 DATA 추출 → AES-GCM 복호화 → JSON 역직렬화
     */
    public <REQ, RES> BpayCallResult<RES> call(String apiName, String path,
                                                REQ detail, Class<RES> responseDetailClass) {
        String url = envConfig.getBaseUrl() + path;
        String requestPlainJson = null;
        String requestEncryptedData = null;
        String responseEncryptedData = null;
        String responsePlainJson = null;

        try {
            // 1. COMM 생성
            String reqDttm = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            CommRequest comm = CommRequest.builder()
                    .appCd(APP_CD)
                    .orgId(ORG_ID)
                    .tno(buildTno(reqDttm))
                    .reqDttm(reqDttm)
                    .build();

            // 2. 요청 객체 생성 및 JSON 직렬화
            BpayApiRequest<REQ> request = BpayApiRequest.<REQ>builder()
                    .comm(comm)
                    .detail(detail)
                    .build();
            requestPlainJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);
            log.info("[BpayClient] {} | REQ plain: {}", apiName, requestPlainJson);

            // 3. COMM + DETAIL 객체 → String → AES-256 GCM 암호화
            //    (Object to String 후 암호화 — CLAUDE.md 요청 흐름 기준)
            String requestPlainCompact = objectMapper.writeValueAsString(request);
            requestEncryptedData = CryptoUtils.encrypt(requestPlainCompact, cryptoProperties.getKeyFor(envConfig.isProd()));
            log.info("[BpayClient] {} | REQ encrypted: {}", apiName, requestEncryptedData);

            // 4. { "DATA": "암호화값" } 을 명시적으로 직렬화하여 HTTP POST
            //    RestTemplate 내부 Jackson에 의존하지 않고 objectMapper로 직접 처리
            String requestBodyJson = objectMapper.writeValueAsString(new DataEnvelope(requestEncryptedData));
            log.debug("[BpayClient] {} | HTTP body: {}", apiName, requestBodyJson);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(requestBodyJson, headers);

            ResponseEntity<String> httpResponse = restTemplate.exchange(
                    url, HttpMethod.POST, entity, String.class);
            String responseBodyJson = httpResponse.getBody();

            if (responseBodyJson == null || responseBodyJson.isBlank()) {
                throw new IllegalStateException("Empty HTTP response body from server");
            }
            log.debug("[BpayClient] {} | HTTP response body: {}", apiName, responseBodyJson);

            // 5. 응답 String → DataEnvelope 역직렬화 → DATA 추출
            DataEnvelope responseEnvelope = objectMapper.readValue(responseBodyJson, DataEnvelope.class);
            if (responseEnvelope.getData() == null) {
                throw new IllegalStateException("Response DATA field is null");
            }
            responseEncryptedData = responseEnvelope.getData();
            log.info("[BpayClient] {} | RES encrypted: {}", apiName, responseEncryptedData);

            // 6. DATA 값(암호화 String) → AES-256 GCM 복호화 → Plain JSON String
            responsePlainJson = CryptoUtils.decrypt(responseEncryptedData, cryptoProperties.getKeyFor(envConfig.isProd()));
            log.info("[BpayClient] {} | RES plain: {}", apiName, responsePlainJson);

            // 7. Plain JSON String → BpayApiResponse 역직렬화
            var responseType = objectMapper.getTypeFactory()
                    .constructParametricType(BpayApiResponse.class, responseDetailClass);
            BpayApiResponse<RES> apiResponse = objectMapper.readValue(responsePlainJson, responseType);
            CommResponse respComm = apiResponse.getComm();

            return BpayCallResult.<RES>builder()
                    .apiName(apiName)
                    .url(url)
                    .requestPlainJson(requestPlainJson)
                    .requestEncryptedData(requestEncryptedData)
                    .responseEncryptedData(responseEncryptedData)
                    .responsePlainJson(responsePlainJson)
                    .rc(respComm.getRc())
                    .rm(respComm.getRm())
                    .resDttm(respComm.getResDttm())
                    .tno(respComm.getTno())
                    .detail(apiResponse.getDetail())
                    .build();

        } catch (Exception e) {
            log.error("[BpayClient] {} failed: {}", apiName, e.getMessage(), e);
            return BpayCallResult.<RES>builder()
                    .apiName(apiName)
                    .url(url)
                    .requestPlainJson(requestPlainJson)
                    .requestEncryptedData(requestEncryptedData)
                    .responseEncryptedData(responseEncryptedData)
                    .responsePlainJson(responsePlainJson)
                    .rc("9999")
                    .rm(e.getMessage())
                    .errorDetail(extractErrorDetail(e))
                    .build();
        }
    }

    private String extractErrorDetail(Exception e) {
        if (e instanceof HttpStatusCodeException httpEx) {
            String body = httpEx.getResponseBodyAsString();
            return String.format("[HTTP %s] %s%s",
                    httpEx.getStatusCode(),
                    httpEx.getMessage(),
                    body.isBlank() ? "" : "\n\nResponse Body:\n" + body);
        }
        // 일반 예외: 원인 체인 포함 전체 메시지
        StringBuilder sb = new StringBuilder(e.toString());
        Throwable cause = e.getCause();
        while (cause != null) {
            sb.append("\nCaused by: ").append(cause.toString());
            cause = cause.getCause();
        }
        return sb.toString();
    }

    /**
     * TNO 생성: ORG_ID(13) + REQ_DTTM(14) + 요청지구분(1) + 랜덤(4) = 32자
     */
    private String buildTno(String reqDttm) {
        String random = String.format("%04d", new Random().nextInt(10000));
        return ORG_ID + reqDttm + REQ_ORIGIN + random;
    }
}
