package com.example.footballmanagement.exception.custom;

import com.example.footballmanagement.exception.ApiException;
import com.example.footballmanagement.exception.ErrorCode;

public class VoucherException extends ApiException {
    public VoucherException(ErrorCode errorCode) {
        super(errorCode);
    }
}
