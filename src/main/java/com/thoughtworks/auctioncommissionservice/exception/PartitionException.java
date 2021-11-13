package com.thoughtworks.auctioncommissionservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class PartitionException extends RuntimeException {
    public PartitionException() {super("系统异常，支付功能暂不可用，请稍后查看支付状态");}
}
