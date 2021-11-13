package com.thoughtworks.auctioncommissionservice.message;

import com.thoughtworks.auctioncommissionservice.service.DelegationService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {

    private final DelegationService delegationService;

    public MessageListener(DelegationService delegationService) {
        this.delegationService = delegationService;
    }

    @JmsListener(destination = "evaluation.center")
    public void receiveMessage(String message) {
        System.out.println("Received <" + message + ">");
    }
}
