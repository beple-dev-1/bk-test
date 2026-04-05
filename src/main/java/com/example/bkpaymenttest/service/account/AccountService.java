package com.example.bkpaymenttest.service.account;

import com.example.bkpaymenttest.client.bpay.BpayClient;
import com.example.bkpaymenttest.dto.common.BpayCallResult;
import com.example.bkpaymenttest.dto.member.MemberCheckDetailRequest;
import com.example.bkpaymenttest.dto.member.MemberCheckDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final BpayClient bpayClient;

    /**
     * 회원여부 조회 — POST /bravo/v1/member/check
     */
    public BpayCallResult<MemberCheckDetailResponse> checkMember(String uuid) {
        log.info("[AccountService] checkMember uuid={}", uuid);
        MemberCheckDetailRequest detail = MemberCheckDetailRequest.builder()
                .uuid(uuid)
                .build();
        return bpayClient.call("회원여부 조회", "/bravo/v1/member/check", detail, MemberCheckDetailResponse.class);
    }
}
