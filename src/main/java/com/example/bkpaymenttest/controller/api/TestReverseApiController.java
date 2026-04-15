package com.example.bkpaymenttest.controller.api;

import com.example.bkpaymenttest.common.crypto.CryptoUtils;
import com.example.bkpaymenttest.config.CryptoProperties;
import com.example.bkpaymenttest.config.EnvConfig;
import com.example.bkpaymenttest.dto.common.*;
import com.example.bkpaymenttest.dto.reverse.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Random;

/**
 * 역거래 API 테스트 프록시 — UI에서 호출
 * COMM + DETAIL 생성 → 암호화 → GATEWAY_040 경유 → 역거래 실제 HTTP POST
 * → 응답 복호화 → BpayCallResult 반환
 *
 * 게이트웨이 호출 형식:
 *   POST https://dev-biz-zero.bizplay.co.kr/GATEWAY_040.act
 *   { "URL": "https://dev-gift.appply.co.kr/{path}", "METHOD": "POST", "PARAMETER": "<DATA>" }
 *
 * 게이트웨이 응답 형식:
 *   { "RES_CD": "...", "RES_MSG": "...", "BODY": { "DATA": "..." } }
 */
@Slf4j
@RestController
@RequestMapping("/api/test/reverse")
@RequiredArgsConstructor
public class TestReverseApiController {

    private static final String APP_CD = "B_JBK";
    private static final String ORG_ID = "PBP2511000011";

    private final CryptoProperties cryptoProperties;
    private final EnvConfig        envConfig;
    private final ObjectMapper     objectMapper;
    private final RestTemplate     restTemplate;

    // ---------------------------------------------------------------
    // 1. 환불결과 통지
    // ---------------------------------------------------------------
    @PostMapping("/refund-noti")
    public ResponseEntity<ApiResponse<BpayCallResult<RefundNotiDetailResponse>>> testRefundNoti(
            @RequestBody Map<String, String> body) {

        RefundNotiDetailRequest detail = RefundNotiDetailRequest.builder()
                .trgtTrxDt(body.get("trgtTrxDt"))
                .trgtTrxSeq(body.get("trgtTrxSeq"))
                .orgTrxDt(body.get("orgTrxDt"))
                .orgTrxSeq(body.get("orgTrxSeq"))
                .amt(body.get("amt"))
                .uuid(body.get("uuid"))
                .build();

        return ResponseEntity.ok(ApiResponse.success(
                call("환불결과 통지", "/api/bpy/v1/refund/noti",
                        detail, RefundNotiDetailResponse.class)));
    }

    // ---------------------------------------------------------------
    // 2. 포인트 거래결과 조회
    // ---------------------------------------------------------------
    @PostMapping("/tran-result")
    public ResponseEntity<ApiResponse<BpayCallResult<TranResultDetailResponse>>> testTranResult(
            @RequestBody Map<String, String> body) {

        TranResultDetailRequest detail = TranResultDetailRequest.builder()
                .trgtTrxDt(body.get("trgtTrxDt"))
                .trgtTrxSeq(body.get("trgtTrxSeq"))
                .orgTrxDt(nullIfBlank(body.get("orgTrxDt")))
                .orgTrxSeq(nullIfBlank(body.get("orgTrxSeq")))
                .type(body.get("type"))
                .uuid(body.get("uuid"))
                .build();

        return ResponseEntity.ok(ApiResponse.success(
                call("포인트 거래결과 조회", "/api/bpy/v1/tran/result",
                        detail, TranResultDetailResponse.class)));
    }

    // ---------------------------------------------------------------
    // 3. 포인트 차감/환불/망취소 요청
    // ---------------------------------------------------------------
    @PostMapping("/pay-request")
    public ResponseEntity<ApiResponse<BpayCallResult<PayRequestDetailResponse>>> testPayRequest(
            @RequestBody Map<String, String> body) {

        PayRequestDetailRequest detail = PayRequestDetailRequest.builder()
                .trxDt(body.get("trxDt"))
                .trxSeq(body.get("trxSeq"))
                .mpmTno(nullIfBlank(body.get("mpmTno")))
                .orgTrxDt(nullIfBlank(body.get("orgTrxDt")))
                .orgTrxSeq(nullIfBlank(body.get("orgTrxSeq")))
                .amt(body.get("amt"))
                .uuid(body.get("uuid"))
                .afltId(body.get("afltId"))
                .afltNm(body.get("afltNm"))
                .type(body.get("type"))
                .build();

        return ResponseEntity.ok(ApiResponse.success(
                call("포인트 차감/환불/망취소 요청", "/api/bpy/v1/pay/request",
                        detail, PayRequestDetailResponse.class)));
    }

    // ---------------------------------------------------------------
    // 4. CPM 결제완료 통지
    // ---------------------------------------------------------------
    @PostMapping("/cpm-complete")
    public ResponseEntity<ApiResponse<BpayCallResult<CpmCompleteDetailResponse>>> testCpmComplete(
            @RequestBody Map<String, String> body) {

        CpmCompletePayInfo payInfo = CpmCompletePayInfo.builder()
                .trxDt(body.get("piTrxDt"))
                .trxTm(body.get("piTrxTm"))
                .trxSeq(body.get("piTrxSeq"))
                .afltId(body.get("piAfltId"))
                .afltNm(body.get("piAfltNm"))
                .amt(body.get("piAmt"))
                .supyAmt(body.get("piSupyAmt"))
                .vat(body.get("piVat"))
                .svcAmt(body.get("piSvcAmt"))
                .bizNo(body.get("piBizNo"))
                .upjongNm(body.get("piUpjongNm"))
                .reprNm(body.get("piReprNm"))
                .telNo(body.get("piTelNo"))
                .addr(body.get("piAddr"))
                .addrDtl(body.get("piAddrDtl"))
                .procSt(body.get("piProcSt"))
                .build();

        CpmCompleteDetailRequest detail = CpmCompleteDetailRequest.builder()
                .uuid(body.get("uuid"))
                .orgTno(body.get("orgTno"))
                .payInfo(payInfo)
                .build();

        return ResponseEntity.ok(ApiResponse.success(
                call("CPM 결제완료 통지", "/api/bpy/v1/cpm/complete",
                        detail, CpmCompleteDetailResponse.class)));
    }

    // ---------------------------------------------------------------
    // 공통 HTTP 호출 헬퍼 — BpayClient 와 동일한 흐름
    // ---------------------------------------------------------------

    /**
     * 역거래 API 실제 HTTP 호출
     * COMM + DETAIL → JSON → AES-GCM 암호화 → POST → 복호화 → BpayCallResult
     */
    private <REQ, RES> BpayCallResult<RES> call(
            String apiName, String path, REQ detail, Class<RES> responseDetailClass) {

        String targetUrl         = envConfig.getReverseBaseUrl() + path;  // GATEWAY 요청부 URL 필드
        String requestPlainJson      = null;
        String requestEncryptedData  = null;
        String responseEncryptedData = null;
        String responsePlainJson     = null;

        try {
            String key = cryptoProperties.getEncryptKey();

            // 1. COMM 생성
            String reqDttm = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            CommRequest comm = CommRequest.builder()
                    .appCd(APP_CD).orgId(ORG_ID)
                    .tno(buildTno(reqDttm)).reqDttm(reqDttm)
                    .build();

            // 2. COMM + DETAIL 직렬화 → 암호화
            BpayApiRequest<REQ> request = BpayApiRequest.<REQ>builder()
                    .comm(comm).detail(detail).build();

            requestPlainJson     = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request);
            String compact       = objectMapper.writeValueAsString(request);
            requestEncryptedData = CryptoUtils.encrypt(compact, key);
            log.info("[TestReverse] {} | REQ plain: {}", apiName, requestPlainJson);
            log.info("[TestReverse] {} | REQ encrypted: {}", apiName, requestEncryptedData);

            // 3. GATEWAY_040 HTTP POST
            //    { "URL": targetUrl, "METHOD": "POST", "PARAMETER": "<암호화값>" }
            String requestBodyJson = objectMapper.writeValueAsString(
                    new GatewayRequest(targetUrl, "POST", new DataEnvelope(requestEncryptedData)));
            log.info("[TestReverse] {} | GATEWAY REQ body: {}", apiName, requestBodyJson);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(requestBodyJson, headers);

            String gatewayUrl = envConfig.getGatewayUrl();
            ResponseEntity<String> httpResponse = restTemplate.exchange(
                    gatewayUrl, HttpMethod.POST, entity, String.class);

            String responseBodyJson = httpResponse.getBody();
            if (responseBodyJson == null || responseBodyJson.isBlank()) {
                throw new IllegalStateException("Empty HTTP response body");
            }
            log.info("[TestReverse] {} | GATEWAY RES body: {}", apiName, responseBodyJson);

            // 4. 게이트웨이 응답 파싱 → BODY(JSON 문자열) → DataEnvelope → DATA 추출 → 복호화
            //    { "RES_CD": "...", "RES_MSG": "...", "BODY": "{\"DATA\":\"...\"}" }
            //    BODY 필드는 DataEnvelope JSON이 문자열로 인코딩되어 전달되므로 한 번 더 역직렬화
            GatewayResponse gatewayResponse = objectMapper.readValue(responseBodyJson, GatewayResponse.class);
            if (gatewayResponse.getBody() == null) {
                throw new IllegalStateException(
                        "Gateway BODY is null — RES_CD=" + gatewayResponse.getResCd()
                        + ", RES_MSG=" + gatewayResponse.getResMsg());
            }
            DataEnvelope bodyEnvelope = objectMapper.readValue(gatewayResponse.getBody(), DataEnvelope.class);
            if (bodyEnvelope.getData() == null) {
                throw new IllegalStateException(
                        "Gateway BODY.DATA is null — RES_CD=" + gatewayResponse.getResCd()
                        + ", RES_MSG=" + gatewayResponse.getResMsg());
            }
            responseEncryptedData = bodyEnvelope.getData();
            log.info("[TestReverse] {} | RES encrypted: {}", apiName, responseEncryptedData);

            responsePlainJson = CryptoUtils.decrypt(responseEncryptedData, key);
            log.info("[TestReverse] {} | RES plain: {}", apiName, responsePlainJson);

            // 5. 역직렬화
            var responseType = objectMapper.getTypeFactory()
                    .constructParametricType(BpayApiResponse.class, responseDetailClass);
            BpayApiResponse<RES> apiResponse = objectMapper.readValue(responsePlainJson, responseType);
            CommResponse respComm = apiResponse.getComm();

            return BpayCallResult.<RES>builder()
                    .apiName(apiName).url(gatewayUrl)
                    .requestPlainJson(requestPlainJson)
                    .requestEncryptedData(requestEncryptedData)
                    .responseEncryptedData(responseEncryptedData)
                    .responsePlainJson(responsePlainJson)
                    .rc(respComm.getRc()).rm(respComm.getRm())
                    .resDttm(respComm.getResDttm()).tno(respComm.getTno())
                    .detail(apiResponse.getDetail())
                    .build();

        } catch (Exception e) {
            log.error("[TestReverse] {} failed: {}", apiName, e.getMessage(), e);
            return BpayCallResult.<RES>builder()
                    .apiName(apiName).url(envConfig.getGatewayUrl())
                    .requestPlainJson(requestPlainJson)
                    .requestEncryptedData(requestEncryptedData)
                    .responseEncryptedData(responseEncryptedData)
                    .responsePlainJson(responsePlainJson)
                    .rc("9999").rm(e.getMessage())
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
        StringBuilder sb = new StringBuilder(e.toString());
        Throwable cause = e.getCause();
        while (cause != null) {
            sb.append("\nCaused by: ").append(cause.toString());
            cause = cause.getCause();
        }
        return sb.toString();
    }

    private String buildTno(String reqDttm) {
        // 역거래: 비플페이 → 이용기관 방향이므로 요청지구분 = B (비플페이 요청)
        return ORG_ID + reqDttm + "B" + String.format("%04d", new Random().nextInt(10000));
    }

    private String nullIfBlank(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}
