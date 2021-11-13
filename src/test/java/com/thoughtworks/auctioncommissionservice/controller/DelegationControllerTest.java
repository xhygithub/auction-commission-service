package com.thoughtworks.auctioncommissionservice.controller;

import com.thoughtworks.auctioncommissionservice.AuctionCommissionServiceApplication;
import com.thoughtworks.auctioncommissionservice.controller.dto.AccountType;
import com.thoughtworks.auctioncommissionservice.controller.dto.PaymentDto;
import com.thoughtworks.auctioncommissionservice.controller.dto.PaymentResponseDto;
import com.thoughtworks.auctioncommissionservice.controller.dto.PaymentStatus;
import com.thoughtworks.auctioncommissionservice.exception.PartitionException;
import com.thoughtworks.auctioncommissionservice.exception.PaymentException;
import com.thoughtworks.auctioncommissionservice.service.DelegationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = AuctionCommissionServiceApplication.class)
public class DelegationControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private DelegationService delegationService;

    //工序1
    @Test
    void should_return_200_when_succeed_to_make_payment_for_unsold_lot() {
        PaymentDto paymentDto = PaymentDto.builder()
                .accountName("zhangsan")
                .accountNumber("121212345")
                .accountType(AccountType.WECHAT)
                .amount(3000.0).build();

        when(delegationService.makePaymentForUnsoldLot(eq(123L), any())).thenReturn(
                PaymentResponseDto.builder().paymentStatus(PaymentStatus.SUCCESS).build()
        );

        HttpEntity<PaymentDto> request = new HttpEntity<>(paymentDto);
        ResponseEntity<PaymentResponseDto> response = restTemplate
                .exchange("/delegation-orders/123/unsold-payment-request/confirmation", HttpMethod.POST, request, PaymentResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        PaymentResponseDto responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }

    @Test
    void should_return_503_when_payment_service_is_unavailable() {
        PaymentDto paymentDto = PaymentDto.builder()
                .accountName("zhangsan")
                .accountNumber("121212345")
                .accountType(AccountType.ALIPAY)
                .amount(3000.0).build();

        when(delegationService.makePaymentForUnsoldLot(eq(123L), any())).thenThrow(
                new PartitionException()
        );

        HttpEntity<PaymentDto> request = new HttpEntity<>(paymentDto);
        ResponseEntity<PaymentResponseDto> response = restTemplate
                .exchange("/delegation-orders/123/unsold-payment-request/confirmation", HttpMethod.POST, request, PaymentResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }

    @Test
    void should_return_400_when_fail_to_make_payment() throws Exception {
        PaymentDto paymentDto = PaymentDto.builder()
                .accountName("zhangsan")
                .accountNumber("121212345")
                .accountType(AccountType.WECHAT)
                .amount(3000.0).build();

        when(delegationService.makePaymentForUnsoldLot(eq(123L), any())).thenThrow(
                new PaymentException("余额不足，支付失败")
        );

        HttpEntity<PaymentDto> request = new HttpEntity<>(paymentDto);
        ResponseEntity<PaymentResponseDto> response = restTemplate
                .exchange("/delegation-orders/123/unsold-payment-request/confirmation", HttpMethod.POST, request, PaymentResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
