package com.thoughtworks.auctioncommissionservice.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.thoughtworks.auctioncommissionservice.controller.dto.PaymentDto;
import com.thoughtworks.auctioncommissionservice.controller.dto.PaymentStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class PaymentClientTest {

    @Autowired
    private PaymentClient paymentClient;

    WireMockServer wm;

    @BeforeEach
    void setUp() {
        wm = new WireMockServer(9000);
        wm.start();
    }

    @AfterEach
    void tearDown() {
        wm.stop();
    }

    @Test //工序三
    void should_make() {
        WireMock.configureFor("localhost", 9000);
        stubFor(post(urlMatching("/payment"))
                .willReturn(aResponse().withHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE)
                        .withBody(PaymentStatus.SUCCESS.name())));

        String paymentStatus = paymentClient.makePayment(PaymentDto.builder().build());
        assertThat(paymentStatus).isEqualTo(PaymentStatus.SUCCESS.name());
    }
}
