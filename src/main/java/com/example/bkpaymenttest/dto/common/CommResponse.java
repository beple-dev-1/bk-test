package com.example.bkpaymenttest.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommResponse {

    @JsonProperty("APP_CD")
    private String appCd;

    @JsonProperty("ORG_ID")
    private String orgId;

    @JsonProperty("TNO")
    private String tno;

    @JsonProperty("RES_DTTM")
    private String resDttm;

    @JsonProperty("RC")
    private String rc;

    @JsonProperty("RM")
    private String rm;
}
