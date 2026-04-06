package com.example.bkpaymenttest.dto.member;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberJoinDetailRequest {

    @JsonProperty("UUID")
    private final String uuid;

    @JsonProperty("TYPE")
    private final String type;

    @JsonProperty("CI")
    private final String ci;

    @JsonProperty("MOB_NO")
    private final String mobNo;

    @JsonProperty("MEMB_NM")
    private final String membNm;

    @JsonProperty("BRT_DT")
    private final String brtDt;

    @JsonProperty("GNDR")
    private final String gndr;

    @JsonProperty("IN_FRN_TP")
    private final String inFrnTp;
}
