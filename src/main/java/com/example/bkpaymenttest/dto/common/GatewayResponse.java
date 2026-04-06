package com.example.bkpaymenttest.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * GATEWAY_040 응답 바디
 * { "RES_CD": "...", "RES_MSG": "...", "BODY": { "DATA": "..." } }
 * BODY 안에 역거래 API 응답부 값(DataEnvelope)이 그대로 포함됨
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GatewayResponse {

    @JsonProperty("RES_CD")
    private String resCd;

    @JsonProperty("RES_MSG")
    private String resMsg;

    @JsonProperty("BODY")
    private DataEnvelope body;

    @JsonProperty("STATUS_CODE")
    private Integer statusCode;

    @JsonProperty("STATUS_MESSAGE")
    private String statusMessage;
}
