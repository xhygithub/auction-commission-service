package com.thoughtworks.auctioncommissionservice.controller;

import com.thoughtworks.auctioncommissionservice.AuctionCommissionServiceApplication;
import com.thoughtworks.auctioncommissionservice.common.AccountType;
import com.thoughtworks.auctioncommissionservice.controller.dto.PaymentDto;
import com.thoughtworks.auctioncommissionservice.controller.dto.PaymentResponseDto;
import com.thoughtworks.auctioncommissionservice.common.PaymentStatus;
import com.thoughtworks.auctioncommissionservice.exception.*;
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
                .accountNumber("12345678")
                .accountType(AccountType.WECHAT)
                .amount(3000.0).build();

        when(delegationService.makePaymentForUnsoldLot(eq(123L), any())).thenReturn(
                PaymentResponseDto.builder()
                        .paymentStatus(PaymentStatus.SUCCESS)
                        .accountType(AccountType.WECHAT)
                        .amount(3000.0)
                        .accountName("zhangsan")
                        .accountNumber("12345678")
                        .build()
        );

        HttpEntity<PaymentDto> request = new HttpEntity<>(paymentDto);
        ResponseEntity<PaymentResponseDto> response = restTemplate
                .exchange("/delegation-orders/123/unsold-payment-request/confirmation", HttpMethod.POST, request, PaymentResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        PaymentResponseDto responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(responseBody.getAccountName()).isEqualTo("zhangsan");
        assertThat(responseBody.getAccountType()).isEqualTo(AccountType.WECHAT);
        assertThat(responseBody.getAmount()).isEqualTo(3000.0);
        assertThat(responseBody.getAccountNumber()).isEqualTo("12345678");
    }

    @Test
    void should_return_503_when_payment_service_is_unavailable() {
        PaymentDto paymentDto = PaymentDto.builder()
                .accountName("zhangsan")
                .accountNumber("12345678")
                .accountType(AccountType.WECHAT)
                .amount(3000.0).build();

        when(delegationService.makePaymentForUnsoldLot(eq(123L), any())).thenThrow(
                new PaymentException()
        );

        HttpEntity<PaymentDto> request = new HttpEntity<>(paymentDto);
        ResponseEntity<ErrorResult> response = restTemplate
                .exchange("/delegation-orders/123/unsold-payment-request/confirmation", HttpMethod.POST, request, ErrorResult.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(ErrorCode.SERVICE_UNAVAILABLE.getValue());
    }

    @Test
    void should_return_insufficient_fee_error_when_fail_to_make_payment() throws Exception {
        PaymentDto paymentDto = PaymentDto.builder()
                .accountName("zhangsan")
                .accountNumber("12345678")
                .accountType(AccountType.WECHAT)
                .amount(3000.0).build();

        when(delegationService.makePaymentForUnsoldLot(eq(123L), any())).thenThrow(
                new InsufficientFeePaymentException()
        );

        HttpEntity<PaymentDto> request = new HttpEntity<>(paymentDto);
        ResponseEntity<ErrorResult> response = restTemplate
                .exchange("/delegation-orders/123/unsold-payment-request/confirmation", HttpMethod.POST, request, ErrorResult.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(ErrorCode.INSUFFICIENT_FEE.getValue());
    }

    @Test
    void should_return_incorrect_account_info_when_fail_to_make_payment() {
        PaymentDto paymentDto = PaymentDto.builder()
                .accountName("zhangsan")
                .accountNumber("000000") //incorrect account
                .accountType(AccountType.WECHAT)
                .amount(3000.0).build();

        when(delegationService.makePaymentForUnsoldLot(eq(123L), any())).thenThrow(
                new IncorrectInfoPaymentException()
        );

        HttpEntity<PaymentDto> request = new HttpEntity<>(paymentDto);
        ResponseEntity<ErrorResult> response = restTemplate
                .exchange("/delegation-orders/123/unsold-payment-request/confirmation", HttpMethod.POST, request, ErrorResult.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(ErrorCode.INCORRECT_ACCOUNT_INFO.getValue());
        assertThat(response.getBody().getMessage()).isEqualTo("支付失败，账号信息有误");
    }

    @Test
    void should_return_200_when_succeed_to_make_request_for_lot_evaluation() {
        PaymentDto paymentDto = PaymentDto.builder()
                .accountName("zhangsan")
                .accountNumber("000000") //incorrect account
                .accountType(AccountType.WECHAT)
                .amount(3000.0).build();

        when(delegationService.sendEvaluationRequest(eq(123L))).thenReturn(true);

        HttpEntity<PaymentDto> request = new HttpEntity<>(paymentDto);
        ResponseEntity<ErrorResult> response = restTemplate
                .exchange("/delegation-orders/123/evaluation-request", HttpMethod.POST, request, ErrorResult.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void should_return_404_when_could_not_find_the_lot() {
        PaymentDto paymentDto = PaymentDto.builder()
                .accountName("zhangsan")
                .accountNumber("000000") //incorrect account
                .accountType(AccountType.WECHAT)
                .amount(3000.0).build();


        when(delegationService.sendEvaluationRequest(eq(123L))).thenThrow(
                new LotNotFoundException()
        );

        HttpEntity<PaymentDto> request = new HttpEntity<>(paymentDto);
        ResponseEntity<ErrorResult> response = restTemplate
                .exchange("/delegation-orders/123/evaluation-request", HttpMethod.POST, request, ErrorResult.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        ErrorResult body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getMessage()).isEqualTo("拍品未上交，请先上交拍品");
    }
}
