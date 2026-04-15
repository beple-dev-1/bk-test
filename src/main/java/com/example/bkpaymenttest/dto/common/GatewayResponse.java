package com.example.bkpaymenttest.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * GATEWAY_040 응답 바디
 * { "RES_CD": "...", "RES_MSG": "...", "BODY": "{\"DATA\":\"...\"}" }
 * BODY 필드는 DataEnvelope JSON이 문자열로 인코딩되어 전달됨
 * → 수신 후 한 번 더 objectMapper.readValue() 하여 DataEnvelope를 얻는다
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

    /** JSON-encoded string: "{\"DATA\":\"<base64>\"}" */
    @JsonProperty("BODY")
    private String body;

    @JsonProperty("STATUS_CODE")
    private Integer statusCode;

    @JsonProperty("STATUS_MESSAGE")
    private String statusMessage;
}
