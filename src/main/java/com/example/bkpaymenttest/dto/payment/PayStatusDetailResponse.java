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
public class PayStatusDetailResponse {

    /**
     * 0000 = 결제완료
     * 0001 = 결제실패
     * 0002 = 결제대기중 (바코드 유효)
     * 0003 = 바코드 만료
     * 0004 = 결제진행중
     */
    @JsonProperty("RES_CD")
    private String resCd;

    @JsonProperty("RES_MSG")
    private String resMsg;

    @JsonProperty("PAY_INFO")
    private PayInfo payInfo;
}
