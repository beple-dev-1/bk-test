package com.example.bkpaymenttest.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MpmPayDetailRequest {

    @JsonProperty("UUID")
    private final String uuid;

    /** QR 검증 API 호출 시 사용한 COMM.TNO */
    @JsonProperty("ORG_TNO")
    private final String orgTno;

    /** QR 검증 응답의 TOKEN */
    @JsonProperty("TOKEN")
    private final String token;

    @JsonProperty("QR_CODE")
    private final String qrCode;

    /** 결제수단: P = 이용기관 자체 결제 포인트 */
    @JsonProperty("PAY_TYPE")
    private final String payType;

    @JsonProperty("AMT")
    private final String amt;
}
