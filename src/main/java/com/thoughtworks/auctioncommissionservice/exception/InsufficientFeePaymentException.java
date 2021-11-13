package com.thoughtworks.auctioncommissionservice.exception;

public class InsufficientFeePaymentException extends RuntimeException {
    public InsufficientFeePaymentException() {
        super("支付失败，余额不足");
    }
}
