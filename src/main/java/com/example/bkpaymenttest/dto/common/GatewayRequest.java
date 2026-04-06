package com.example.bkpaymenttest.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * GATEWAY_040 요청 바디
 * { "URL": "...", "METHOD": "POST", "PARAMETER": { "DATA": "BASE64_AESGCM_ENCRYPTED_STRING" } }
 */
@Getter
@AllArgsConstructor
public class GatewayRequest {

    @JsonProperty("URL")
    private String url;

    @JsonProperty("METHOD")
    private String method;

    @JsonProperty("PARAMETER")
    private DataEnvelope parameter;
}
