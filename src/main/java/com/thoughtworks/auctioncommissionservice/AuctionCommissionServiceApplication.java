package com.thoughtworks.auctioncommissionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AuctionCommissionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuctionCommissionServiceApplication.class, args);
    }
}
