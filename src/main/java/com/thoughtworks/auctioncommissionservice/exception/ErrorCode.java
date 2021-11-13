package com.thoughtworks.auctioncommissionservice.exception;

import java.util.Arrays;

public enum ErrorCode {
    SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE"),
    NOT_FOUND("NOT_FOUND"),
    INSUFFICIENT_FEE("INSUFFICIENT_FEE"),
    INCORRECT_ACCOUNT_INFO("INCORRECT_ACCOUNT_INFO");

    private String value;

    ErrorCode(String errCode) {
        this.value = errCode;
    }
    public String getValue() {
        return value;
    }
}
