package com.thoughtworks.auctioncommissionservice.exception;

public class PaymentException extends RuntimeException {
    public PaymentException() {
        super("系统异常，支付功能暂不可用，请稍后查看支付状态");
    }

    public PaymentException(String msg) {
        super(msg);
    }
}
