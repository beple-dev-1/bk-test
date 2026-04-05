package com.example.bkpaymenttest.common.exception;

import com.example.bkpaymenttest.common.message.MessageCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final MessageCode messageCode;

    public BusinessException(MessageCode messageCode) {
        super(messageCode.getMessage());
        this.messageCode = messageCode;
    }

    public BusinessException(MessageCode messageCode, String detail) {
        super(detail);
        this.messageCode = messageCode;
    }
}
