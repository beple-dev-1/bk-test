package com.example.bkpaymenttest.controller.api;

import com.example.bkpaymenttest.dto.common.ApiResponse;
import com.example.bkpaymenttest.dto.common.BpayCallResult;
import com.example.bkpaymenttest.dto.payment.MpmPayDetailResponse;
import com.example.bkpaymenttest.dto.payment.PayStatusDetailResponse;
import com.example.bkpaymenttest.dto.payment.QrCreateDetailResponse;
import com.example.bkpaymenttest.dto.payment.QrVerifyDetailResponse;
import com.example.bkpaymenttest.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentApiController {

    private final PaymentService paymentService;

    @PostMapping("/qr/create")
    public ResponseEntity<ApiResponse<BpayCallResult<QrCreateDetailResponse>>> createQr(
            @RequestBody Map<String, String> body) {
        String uuid = body.get("uuid");
        BpayCallResult<QrCreateDetailResponse> result = paymentService.createQr(uuid);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/qr/verify")
    public ResponseEntity<ApiResponse<BpayCallResult<QrVerifyDetailResponse>>> verifyQr(
            @RequestBody Map<String, String> body) {
        String uuid    = body.get("uuid");
        String qrCode  = body.get("qrCode");
        BpayCallResult<QrVerifyDetailResponse> result = paymentService.verifyQr(uuid, qrCode);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/mpm")
    public ResponseEntity<ApiResponse<BpayCallResult<MpmPayDetailResponse>>> mpmPay(
            @RequestBody Map<String, String> body) {
        String uuid   = body.get("uuid");
        String orgTno = body.get("orgTno");   // QR 검증 요청의 COMM.TNO
        String token  = body.get("token");    // QR 검증 응답의 TOKEN
        String qrCode = body.get("qrCode");   // 검증에 사용한 QR 코드
        String amt    = body.get("amt");
        BpayCallResult<MpmPayDetailResponse> result = paymentService.mpmPay(uuid, orgTno, token, qrCode, amt);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 결제상태조회
     * MPM: type=M, orgTno=MPM 결제요청 TNO
     * CPM: type=C, orgTno=QR 생성 TNO (CPM 개발 후 사용)
     */
    @PostMapping("/status")
    public ResponseEntity<ApiResponse<BpayCallResult<PayStatusDetailResponse>>> payStatus(
            @RequestBody Map<String, String> body) {
        String uuid   = body.get("uuid");
        String type   = body.get("type");
        String orgTno = body.get("orgTno");
        BpayCallResult<PayStatusDetailResponse> result = paymentService.payStatus(uuid, type, orgTno);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
