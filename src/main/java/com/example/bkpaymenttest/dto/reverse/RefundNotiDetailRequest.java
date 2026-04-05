package com.example.bkpaymenttest.dto.reverse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RefundNotiDetailRequest {

    @JsonProperty("TRGT_TRX_DT")
    private String trgtTrxDt;

    @JsonProperty("TRGT_TRX_SEQ")
    private String trgtTrxSeq;

    @JsonProperty("ORG_TRX_DT")
    private String orgTrxDt;

    @JsonProperty("ORG_TRX_SEQ")
    private String orgTrxSeq;

    @JsonProperty("AMT")
    private String amt;

    @JsonProperty("UUID")
    private String uuid;
}
