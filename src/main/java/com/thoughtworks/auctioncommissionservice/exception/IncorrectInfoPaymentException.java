package com.thoughtworks.auctioncommissionservice.exception;

public class IncorrectInfoPaymentException extends RuntimeException {
    public IncorrectInfoPaymentException() {
        super("支付失败，账号信息有误");
    }
}
