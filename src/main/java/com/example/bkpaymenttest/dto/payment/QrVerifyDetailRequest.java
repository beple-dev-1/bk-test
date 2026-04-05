package com.example.bkpaymenttest.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QrVerifyDetailRequest {

    @JsonProperty("UUID")
    private final String uuid;

    @JsonProperty("QR_CODE")
    private final String qrCode;
}
