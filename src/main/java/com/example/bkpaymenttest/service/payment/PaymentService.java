package com.example.bkpaymenttest.service.payment;

import com.example.bkpaymenttest.client.bpay.BpayClient;
import com.example.bkpaymenttest.dto.common.BpayCallResult;
import com.example.bkpaymenttest.dto.payment.*;
import com.example.bkpaymenttest.dto.payment.PayStatusDetailRequest;
import com.example.bkpaymenttest.dto.payment.PayStatusDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final BpayClient bpayClient;

    /**
     * QR 검증 — POST /bravo/v1/pay/qr/verify
     */
    public BpayCallResult<QrVerifyDetailResponse> verifyQr(String uuid, String qrCode) {
        log.info("[PaymentService] verifyQr uuid={}, qrCode={}", uuid, qrCode);
        QrVerifyDetailRequest detail = QrVerifyDetailRequest.builder()
                .uuid(uuid)
                .qrCode(qrCode)
                .build();
        return bpayClient.call("QR 검증", "/bravo/v1/pay/qr/verify", detail, QrVerifyDetailResponse.class);
    }

    /**
     * MPM 결제요청 — POST /bravo/v1/pay/mpm
     *
     * @param orgTno  QR 검증 API 호출 시 사용한 COMM.TNO
     * @param token   QR 검증 응답의 TOKEN
     * @param qrCode  검증에 사용한 가맹점 QR 코드
     */
    public BpayCallResult<MpmPayDetailResponse> mpmPay(
            String uuid, String orgTno, String token, String qrCode, String amt) {
        log.info("[PaymentService] mpmPay uuid={}, orgTno={}, qrCode={}, amt={}", uuid, orgTno, qrCode, amt);
        MpmPayDetailRequest detail = MpmPayDetailRequest.builder()
                .uuid(uuid)
                .orgTno(orgTno)
                .token(token)
                .qrCode(qrCode)
                .payType("P")
                .amt(amt)
                .build();
        return bpayClient.call("MPM 결제요청", "/bravo/v1/pay/mpm", detail, MpmPayDetailResponse.class);
    }

    /**
     * QR/BAR코드 생성요청 (CPM) — POST /bravo/v1/pay/qr/create
     * PAY_TYPE: P = 이용기관 자체 결제 포인트
     */
    public BpayCallResult<QrCreateDetailResponse> createQr(String uuid) {
        log.info("[PaymentService] createQr uuid={}", uuid);
        QrCreateDetailRequest detail = QrCreateDetailRequest.builder()
                .uuid(uuid)
                .payType("P")
                .build();
        return bpayClient.call("QR/BAR코드 생성", "/bravo/v1/pay/qr/create", detail, QrCreateDetailResponse.class);
    }

    /**
     * 결제상태조회 — POST /bravo/v1/pay/status
     *
     * @param type   M = MPM (ORG_TNO = MPM 결제요청 TNO)
     *               C = CPM (ORG_TNO = QR 생성 TNO) — CPM 개발 시 사용
     * @param orgTno TYPE에 따른 원거래 전문추적번호
     */
    public BpayCallResult<PayStatusDetailResponse> payStatus(String uuid, String type, String orgTno) {
        log.info("[PaymentService] payStatus uuid={}, type={}, orgTno={}", uuid, type, orgTno);
        PayStatusDetailRequest detail = PayStatusDetailRequest.builder()
                .uuid(uuid)
                .type(type)
                .orgTno(orgTno)
                .build();
        return bpayClient.call("결제상태조회", "/bravo/v1/pay/status", detail, PayStatusDetailResponse.class);
    }
}
