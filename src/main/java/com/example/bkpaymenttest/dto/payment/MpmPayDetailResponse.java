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
public class MpmPayDetailResponse {

    @JsonProperty("RES_CD")
    private String resCd;

    @JsonProperty("RES_MSG")
    private String resMsg;

    @JsonProperty("PAY_INFO")
    private PayInfo payInfo;
}
