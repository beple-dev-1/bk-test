package com.example.bkpaymenttest.dto.member;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberCheckDetailRequest {

    @JsonProperty("UUID")
    private final String uuid;
}
