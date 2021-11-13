package com.thoughtworks.auctioncommissionservice.service;

import com.thoughtworks.auctioncommissionservice.client.PaymentClient;
import com.thoughtworks.auctioncommissionservice.common.AuctionStatus;
import com.thoughtworks.auctioncommissionservice.common.KeepingStatus;
import com.thoughtworks.auctioncommissionservice.controller.dto.PaymentDto;
import com.thoughtworks.auctioncommissionservice.controller.dto.PaymentResponseDto;
import com.thoughtworks.auctioncommissionservice.common.PaymentStatus;
import com.thoughtworks.auctioncommissionservice.exception.*;
import com.thoughtworks.auctioncommissionservice.message.JmsSender;
import com.thoughtworks.auctioncommissionservice.repository.DelegationRepository;
import com.thoughtworks.auctioncommissionservice.repository.entity.Delegation;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.nio.charset.Charset;
import java.util.HashMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
public class DelegationServiceTest {
    @Mock
    private PaymentClient paymentClient;

    @InjectMocks
    private DelegationService delegationService;

    @Mock
    private DelegationRepository delegationRepository;

    @Mock
    private JmsSender jmsSender;

    // 工序2
    @Test
    void should_make_payment_and_store_payment_result_into_db() {
        when(paymentClient.makePayment(any())).thenReturn(PaymentStatus.SUCCESS.name());
        when(delegationRepository.findByLotId(eq(123L))).thenReturn(
                Delegation.builder()
                        .lotId(123L)
                        .amount(600.00)
                        .auctionStatus(AuctionStatus.UNSOLD)
                        .payTime(null)
                        .paymentStatus(null)
                        .build()
        );
        when(delegationRepository.save(any())).thenReturn(Delegation.builder().paymentStatus(PaymentStatus.SUCCESS).build());

        PaymentDto build = PaymentDto.builder().amount(600.00).build();
        PaymentResponseDto paymentResponseDto = delegationService.makePaymentForUnsoldLot(123L, build);
        assertThat(paymentResponseDto.getPaymentStatus()).isEqualTo(PaymentStatus.SUCCESS);
    }

    @Test
    void should_throw_payment_exception_when_failed_to_make_payment() {
        String errorMessage = "system error";
        String errorJson =
                "{"
                        + "    \"com.thoughtworks.auctioncommissionservice.message\": \""
                        + errorMessage
                        + "\" ,"
                        + "    \"error_code\": \""
                        + 400
                        + "\""
                        + "  }";
        Request request = Request.create(
                Request.HttpMethod.POST,
                "localhost",
                new HashMap<>(),
                Request.Body.empty(),
                new RequestTemplate());
        Response response =
                Response.builder()
                        .request(request)
                        .status(HttpStatus.BAD_REQUEST.value())
                        .headers(new HashMap<>())
                        .body(errorJson, Charset.defaultCharset())
                        .build();

        when(delegationRepository.findByLotId(eq(123L))).thenReturn(
                Delegation.builder()
                        .lotId(123L)
                        .amount(600.00)
                        .auctionStatus(AuctionStatus.UNSOLD)
                        .payTime(null)
                        .paymentStatus(null)
                        .build()
        );
        when(paymentClient.makePayment(any())).thenThrow(FeignException.errorStatus("", response));
        when(delegationRepository.save(any())).thenReturn(Delegation.builder().paymentStatus(PaymentStatus.SUCCESS).build());

        PaymentDto paymentInfo = PaymentDto.builder().amount(600.00).build();

        PaymentException paymentException = assertThrows(
                PaymentException.class,
                () -> delegationService.makePaymentForUnsoldLot(123L, paymentInfo)
        );
        assertThat(paymentException.getMessage()).isEqualTo("系统异常，支付功能暂不可用，请稍后查看支付状态");
    }

    @Test
    void should_throw_insufficient_fee_exception_when_no_enough_money() {
        String errorMessage = ErrorCode.INSUFFICIENT_FEE.getValue() + "error";
        String errorJson =
                "{"
                        + "    \"com.thoughtworks.auctioncommissionservice.message\": \""
                        + errorMessage
                        + "\" ,"
                        + "    \"error_code\": \""
                        + 408
                        + "\""
                        + "  }";
        Request request = Request.create(
                Request.HttpMethod.POST,
                "localhost",
                new HashMap<>(),
                Request.Body.empty(),
                new RequestTemplate());
        Response response =
                Response.builder()
                        .request(request)
                        .status(HttpStatus.REQUEST_TIMEOUT.value())
                        .headers(new HashMap<>())
                        .body(errorJson, Charset.defaultCharset())
                        .build();

        when(delegationRepository.findByLotId(eq(123L))).thenReturn(
                Delegation.builder()
                        .lotId(123L)
                        .amount(600.00)
                        .auctionStatus(AuctionStatus.UNSOLD)
                        .payTime(null)
                        .paymentStatus(null)
                        .build()
        );
        when(paymentClient.makePayment(any())).thenThrow(FeignException.errorStatus("", response));
        when(delegationRepository.save(any())).thenReturn(Delegation.builder().paymentStatus(PaymentStatus.SUCCESS).build());

        PaymentDto paymentInfo = PaymentDto.builder().amount(600.00).build();

        InsufficientFeePaymentException insufficientFeePaymentException = assertThrows(
                InsufficientFeePaymentException.class,
                () -> delegationService.makePaymentForUnsoldLot(123L, paymentInfo)
        );
        assertThat(insufficientFeePaymentException.getMessage()).isEqualTo("支付失败，余额不足");
    }

    @Test
    void should_throw_incorrect_account_info_exception_when_no_enough_money() {
        String errorMessage = ErrorCode.INCORRECT_ACCOUNT_INFO.getValue() + "error";
        String errorJson =
                "{"
                        + "    \"com.thoughtworks.auctioncommissionservice.message\": \""
                        + errorMessage
                        + "\" ,"
                        + "    \"error_code\": \""
                        + 408
                        + "\""
                        + "  }";
        Request request = Request.create(
                Request.HttpMethod.POST,
                "localhost",
                new HashMap<>(),
                Request.Body.empty(),
                new RequestTemplate());
        Response response =
                Response.builder()
                        .request(request)
                        .status(HttpStatus.REQUEST_TIMEOUT.value())
                        .headers(new HashMap<>())
                        .body(errorJson, Charset.defaultCharset())
                        .build();

        when(delegationRepository.findByLotId(eq(123L))).thenReturn(
                Delegation.builder()
                        .lotId(123L)
                        .amount(600.00)
                        .auctionStatus(AuctionStatus.UNSOLD)
                        .payTime(null)
                        .paymentStatus(null)
                        .build()
        );
        when(paymentClient.makePayment(any())).thenThrow(FeignException.errorStatus("", response));
        when(delegationRepository.save(any())).thenReturn(Delegation.builder().paymentStatus(PaymentStatus.SUCCESS).build());

        PaymentDto paymentInfo = PaymentDto.builder().amount(600.00).build();

        IncorrectInfoPaymentException incorrectInfoPaymentException = assertThrows(
                IncorrectInfoPaymentException.class,
                () -> delegationService.makePaymentForUnsoldLot(123L, paymentInfo)
        );
        assertThat(incorrectInfoPaymentException.getMessage()).isEqualTo("支付失败，账号信息有误");
    }

    @Test
    void should_return_true_when_request_is_sent_successfully() {
        when(delegationRepository.findByLotId(eq(123L)))
                .thenReturn(Delegation.builder().keepingStatus(KeepingStatus.KEEPING).build());
        doNothing().when(jmsSender).sendEvaluationRequest(eq(123L));

        Boolean result = delegationService.sendEvaluationRequest(123L);
        assertThat(result).isTrue();
    }

    @Test
    void should_throw_not_found_exception_when_lot_keeping_status_is_not_keeping() {
        when(delegationRepository.findByLotId(eq(123L)))
                .thenReturn(Delegation.builder().keepingStatus(KeepingStatus.UN_KEEPING).build());
        doNothing().when(jmsSender).sendEvaluationRequest(eq(123L));

        LotNotFoundException lotNotFoundException = assertThrows(LotNotFoundException.class, () ->
                delegationService.sendEvaluationRequest(123L)
        );
        assertThat(lotNotFoundException.getMessage()).isEqualTo("拍品未上交，请先上交拍品");
    }
}
