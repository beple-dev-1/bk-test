package com.example.bkpaymenttest.dto.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayInfo {

    @JsonProperty("TRX_DT")
    private String trxDt;

    @JsonProperty("TRX_TM")
    private String trxTm;

    @JsonProperty("TRX_SEQ")
    private String trxSeq;

    @JsonProperty("AFLT_ID")
    private String afltId;

    @JsonProperty("AFLT_NM")
    private String afltNm;

    @JsonProperty("AMT")
    private String amt;

    @JsonProperty("SUPY_AMT")
    private String supyAmt;

    @JsonProperty("VAT")
    private String vat;

    @JsonProperty("SVC_AMT")
    private String svcAmt;

    @JsonProperty("BIZ_NO")
    private String bizNo;

    @JsonProperty("UPJONG_NM")
    private String upjongNm;

    @JsonProperty("REPR_NM")
    private String reprNm;

    @JsonProperty("TEL_NO")
    private String telNo;

    @JsonProperty("ADDR")
    private String addr;

    @JsonProperty("ADDR_DTL")
    private String addrDtl;
}
