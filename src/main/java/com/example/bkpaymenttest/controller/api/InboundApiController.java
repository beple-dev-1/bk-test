package com.example.bkpaymenttest.controller.api;

import com.example.bkpaymenttest.common.crypto.CryptoUtils;
import com.example.bkpaymenttest.config.CryptoProperties;
import com.example.bkpaymenttest.dto.common.*;
import com.example.bkpaymenttest.dto.reverse.*;
import com.example.bkpaymenttest.service.reverse.ReverseApiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 비플페이 → 이용기관 수신 엔드포인트
 * 요청/응답 모두 { "DATA": "AES-GCM 암호화 값" } 형식
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class InboundApiController {

    private static final String APP_CD = "B_JBK";
    private static final String ORG_ID = "PBP2511000011";

    private final CryptoProperties  cryptoProperties;
    private final ObjectMapper      objectMapper;
    private final ReverseApiService reverseApiService;

    @PostMapping("/api/bpy/v1/refund/noti")
    public ResponseEntity<DataEnvelope> refundNoti(@RequestBody DataEnvelope envelope) {
        return handle(envelope, RefundNotiDetailRequest.class,
                reverseApiService::processRefundNoti, "환불결과 통지");
    }

    @PostMapping("/api/bpy/v1/tran/result")
    public ResponseEntity<DataEnvelope> tranResult(@RequestBody DataEnvelope envelope) {
        return handle(envelope, TranResultDetailRequest.class,
                reverseApiService::processTranResult, "포인트 거래결과 조회");
    }

    @PostMapping("/api/bpy/v1/pay/request")
    public ResponseEntity<DataEnvelope> payRequest(@RequestBody DataEnvelope envelope) {
        return handle(envelope, PayRequestDetailRequest.class,
                reverseApiService::processPayRequest, "포인트 차감/환불/망취소 요청");
    }

    @PostMapping("/api/bpy/v1/cpm/complete")
    public ResponseEntity<DataEnvelope> cpmComplete(@RequestBody DataEnvelope envelope) {
        return handle(envelope, CpmCompleteDetailRequest.class,
                reverseApiService::processCpmComplete, "CPM 결제완료 통지");
    }

    // ---------------------------------------------------------------

    private <REQ, RES> ResponseEntity<DataEnvelope> handle(
            DataEnvelope envelope, Class<REQ> reqClass,
            java.util.function.Function<REQ, RES> serviceCall, String apiName) {
        try {
            String key = cryptoProperties.getEncryptKey();

            // 복호화
            String plainJson = CryptoUtils.decrypt(envelope.getData(), key);
            log.info("[Inbound] {} | REQ plain: {}", apiName, plainJson);

            var reqType = objectMapper.getTypeFactory()
                    .constructParametricType(BpayApiRequest.class, reqClass);
            BpayApiRequest<REQ> request = objectMapper.readValue(plainJson, reqType);

            // 서비스 호출
            RES responseDetail = serviceCall.apply(request.getDetail());

            // 응답 COMM 생성
            String resDttm = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            CommResponse responseComm = new CommResponse();
            responseComm.setAppCd(APP_CD);
            responseComm.setOrgId(ORG_ID);
            responseComm.setTno(request.getComm().getTno());
            responseComm.setResDttm(resDttm);
            responseComm.setRc("0000");
            responseComm.setRm("정상");

            BpayApiResponse<RES> response = new BpayApiResponse<>();
            response.setComm(responseComm);
            response.setDetail(responseDetail);

            String responsePlainJson = objectMapper.writeValueAsString(response);
            log.info("[Inbound] {} | RES plain: {}", apiName, responsePlainJson);

            String responseEncrypted = CryptoUtils.encrypt(responsePlainJson, key);
            return ResponseEntity.ok(new DataEnvelope(responseEncrypted));

        } catch (Exception e) {
            log.error("[Inbound] {} error: {}", apiName, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
