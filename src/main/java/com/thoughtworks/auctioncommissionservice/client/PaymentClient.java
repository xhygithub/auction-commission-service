package com.thoughtworks.auctioncommissionservice.client;


import com.thoughtworks.auctioncommissionservice.controller.dto.PaymentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url="http://localhost:9000")
public interface PaymentClient {

    @PostMapping(value="/payment")
    String makePayment(@RequestBody PaymentDto paymentInfo);
}
