package com.example.bkpaymenttest.dto.reverse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TranResultDetailRequest {

    @JsonProperty("TRGT_TRX_DT")
    private String trgtTrxDt;

    @JsonProperty("TRGT_TRX_SEQ")
    private String trgtTrxSeq;

    @JsonProperty("ORG_TRX_DT")
    private String orgTrxDt;

    @JsonProperty("ORG_TRX_SEQ")
    private String orgTrxSeq;

    /** 1=차감, 2=환불, 3=차감망취소(미사용), 4=환불망취소(미사용) */
    @JsonProperty("TYPE")
    private String type;

    @JsonProperty("UUID")
    private String uuid;
}
