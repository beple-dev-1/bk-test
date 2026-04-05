package com.example.bkpaymenttest.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QrCreateDetailRequest {

    @JsonProperty("UUID")
    private final String uuid;

    /** 결제수단: P = 이용기관 자체 결제 포인트 */
    @JsonProperty("PAY_TYPE")
    private final String payType;
}
