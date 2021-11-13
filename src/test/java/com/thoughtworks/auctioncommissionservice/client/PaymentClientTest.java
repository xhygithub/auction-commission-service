package com.thoughtworks.auctioncommissionservice.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import com.thoughtworks.auctioncommissionservice.controller.dto.PaymentDto;
import com.thoughtworks.auctioncommissionservice.common.PaymentStatus;
import feign.FeignException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void should_return_success_when_mock_server_return_success() {
        WireMock.configureFor("localhost", 9000);
        stubFor(post(urlMatching("/payment"))
                .willReturn(aResponse().withHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE)
                        .withBody(PaymentStatus.SUCCESS.name())));

        String paymentStatus = paymentClient.makePayment(PaymentDto.builder().build());
        assertThat(paymentStatus).isEqualTo(PaymentStatus.SUCCESS.name());
    }

    @Test //工序三
    void should_throw_exception_when_mock_server_throw_exception() {
        WireMock.configureFor("localhost", 9000);
        stubFor(post(urlMatching("/payment"))
                .willReturn(aResponse().withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

        assertThrows(FeignException.class,
                () -> paymentClient.makePayment(PaymentDto.builder().build()));
    }
}
