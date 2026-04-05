package com.example.bkpaymenttest.dto.reconciliation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReconciliationRequest {

    // TODO: add reconciliation request fields
    private String fromDate;
    private String toDate;
    private String transactionId;
}
