package com.thoughtworks.auctioncommissionservice.message;

import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@EnableJms
@Component
public class JmsSender {
    private final JmsTemplate jmsTemplate;

    public JmsSender(JmsTemplate jmsTemplate) {
        this.jmsTemplate  = jmsTemplate;
    }

    public void sendEvaluationRequest(Long lotId) {
        jmsTemplate.convertAndSend("evaluation.center", lotId);
    }
}
