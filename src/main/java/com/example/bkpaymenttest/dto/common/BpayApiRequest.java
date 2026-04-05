package com.example.bkpaymenttest.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BpayApiRequest<T> {

    @JsonProperty("COMM")
    private final CommRequest comm;

    @JsonProperty("DETAIL")
    private final T detail;
}
