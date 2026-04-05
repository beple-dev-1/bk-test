package com.example.bkpaymenttest.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BpayApiResponse<T> {

    @JsonProperty("COMM")
    private CommResponse comm;

    @JsonProperty("DETAIL")
    private T detail;
}
