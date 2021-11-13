package com.thoughtworks.auctioncommissionservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class LotNotFoundException extends RuntimeException {
    public LotNotFoundException() {
        super("拍品未上交，请先上交拍品");
    }

    public LotNotFoundException(String msg) {
        super(msg);
    }
}
