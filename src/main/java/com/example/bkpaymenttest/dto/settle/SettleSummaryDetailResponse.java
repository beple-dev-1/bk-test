package com.example.bkpaymenttest.dto.settle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SettleSummaryDetailResponse {

    @JsonProperty("RES_CD")
    private String resCd;

    @JsonProperty("RES_MSG")
    private String resMsg;

    @JsonProperty("ORG_SUMMARY_INFO")
    private SummaryInfo orgSummaryInfo;

    @JsonProperty("BP_SUMMARY_INFO")
    private SummaryInfo bpSummaryInfo;
}
