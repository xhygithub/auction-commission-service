package com.thoughtworks.auctioncommissionservice.service;

import com.thoughtworks.auctioncommissionservice.client.PaymentClient;
import com.thoughtworks.auctioncommissionservice.controller.dto.PaymentDto;
import com.thoughtworks.auctioncommissionservice.controller.dto.PaymentResponseDto;
import com.thoughtworks.auctioncommissionservice.controller.dto.PaymentStatus;
import com.thoughtworks.auctioncommissionservice.exception.LotNotFoundException;
import com.thoughtworks.auctioncommissionservice.exception.PartitionException;
import com.thoughtworks.auctioncommissionservice.exception.PaymentException;
import com.thoughtworks.auctioncommissionservice.message.JmsSender;
import com.thoughtworks.auctioncommissionservice.repository.DelegationRepository;
import com.thoughtworks.auctioncommissionservice.repository.entity.Delegation;
import com.thoughtworks.auctioncommissionservice.controller.dto.KeepingStatus;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
@Slf4j
public class DelegationService {
    private final PaymentClient paymentClient;
    private final DelegationRepository delegationRepository;
    private final JmsSender jmsSender;

    public PaymentResponseDto makePaymentForUnsoldLot(Long orderId, PaymentDto paymentInfo) {
        Delegation delegation = delegationRepository.findByLotId(orderId);
        try {
            paymentClient.makePayment(paymentInfo);
            delegation.setPaymentStatus(PaymentStatus.SUCCESS);
            delegation.setPayTime(new Date().getTime());
            delegationRepository.save(delegation);
            return PaymentResponseDto.builder()
                    .accountNumber(paymentInfo.getAccountName())
                    .accountType(paymentInfo.getAccountType())
                    .accountName(paymentInfo.getAccountName())
                    .paymentStatus(delegation.getPaymentStatus()).build();
        } catch (FeignException e) {
            if (e.status() == HttpStatus.SERVICE_UNAVAILABLE.value() || e.status() == HttpStatus.REQUEST_TIMEOUT.value()) {
                log.info("partition error: {}", orderId);
                throw new PartitionException();
            }

            if(e.getMessage().equals("INSUFFICIENT_FEE")) {
                log.info("failed to make a payment for lot: {}", orderId);
                throw new PaymentException("支付失败");
            }
            log.info("failed to make a payment for lot: {}", orderId);
            throw new PaymentException("支付失败");
        }
    }

    public void sendEvaluationRequest(Long orderId) {
        Delegation byLotId = delegationRepository.findByLotId(orderId);
        if(!byLotId.getKeepingStatus().equals(KeepingStatus.KEEPING)) {
            throw new LotNotFoundException("拍品未上交，请先上交拍品");
        }
        jmsSender.sendEvaluationRequest(orderId);
    }
}
