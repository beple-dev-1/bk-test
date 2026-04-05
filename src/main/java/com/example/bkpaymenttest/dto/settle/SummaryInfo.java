package com.example.bkpaymenttest.dto.settle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SummaryInfo {

    @JsonProperty("TOT_PAY_CNT")
    private String totPayCnt;

    @JsonProperty("TOT_PAY_AMT")
    private String totPayAmt;

    @JsonProperty("TOT_RFND_CNT")
    private String totRfndCnt;

    @JsonProperty("TOT_RFND_AMT")
    private String totRfndAmt;

    @JsonProperty("TOT_CNCL_CNT")
    private String totCnclCnt;

    @JsonProperty("TOT_CNCL_AMT")
    private String totCnclAmt;

    @JsonProperty("TOT_RFND_CNCL_CNT")
    private String totRfndCnclCnt;

    @JsonProperty("TOT_RFND_CNCL_AMT")
    private String totRfndCnclAmt;
}
