package com.example.bkpaymenttest.dto.settle;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SettleSummaryDetailRequest {

    @JsonProperty("STR_DATE")
    private String strDate;

    @JsonProperty("END_DATE")
    private String endDate;

    @JsonProperty("ORG_SUMMARY_INFO")
    private SummaryInfo orgSummaryInfo;
}
