package com.example.bkpaymenttest.dto.reverse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayRequestDetailRequest {

    @JsonProperty("TRX_DT")
    private String trxDt;

    @JsonProperty("TRX_SEQ")
    private String trxSeq;

    /** TYPE=1(차감 MPM)인 경우만 */
    @JsonProperty("MPM_TNO")
    private String mpmTno;

    @JsonProperty("ORG_TRX_DT")
    private String orgTrxDt;

    @JsonProperty("ORG_TRX_SEQ")
    private String orgTrxSeq;

    @JsonProperty("AMT")
    private String amt;

    @JsonProperty("UUID")
    private String uuid;

    @JsonProperty("AFLT_ID")
    private String afltId;

    @JsonProperty("AFLT_NM")
    private String afltNm;

    /** 1=차감(MPM), 2=차감(CPM), 3=환불, 4=차감망취소, 5=환불망취소 */
    @JsonProperty("TYPE")
    private String type;
}
