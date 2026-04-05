package com.example.bkpaymenttest.dto.reverse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RefundNotiDetailResponse {

    @JsonProperty("RES_CD")
    private String resCd;

    @JsonProperty("RES_MSG")
    private String resMsg;
}
