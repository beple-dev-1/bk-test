package com.example.bkpaymenttest.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommRequest {

    @JsonProperty("APP_CD")
    private final String appCd;

    @JsonProperty("ORG_ID")
    private final String orgId;

    @JsonProperty("TNO")
    private final String tno;

    @JsonProperty("REQ_DTTM")
    private final String reqDttm;
}
