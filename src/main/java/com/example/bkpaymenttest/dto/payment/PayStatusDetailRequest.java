package com.example.bkpaymenttest.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PayStatusDetailRequest {

    @JsonProperty("UUID")
    private final String uuid;

    /**
     * 조회 유형
     * C = CPM (QR/바코드) → ORG_TNO = QR 생성 시 TNO
     * M = MPM             → ORG_TNO = MPM 결제요청 시 TNO
     */
    @JsonProperty("TYPE")
    private final String type;

    @JsonProperty("ORG_TNO")
    private final String orgTno;
}
