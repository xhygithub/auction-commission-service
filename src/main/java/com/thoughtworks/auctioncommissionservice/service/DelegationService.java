package com.thoughtworks.auctioncommissionservice.service;

import com.thoughtworks.auctioncommissionservice.client.PaymentClient;
import com.thoughtworks.auctioncommissionservice.controller.dto.PaymentDto;
import com.thoughtworks.auctioncommissionservice.controller.dto.PaymentResponseDto;
import com.thoughtworks.auctioncommissionservice.common.PaymentStatus;
import com.thoughtworks.auctioncommissionservice.exception.*;
import com.thoughtworks.auctioncommissionservice.message.JmsSender;
import com.thoughtworks.auctioncommissionservice.repository.DelegationRepository;
import com.thoughtworks.auctioncommissionservice.repository.entity.Delegation;
import com.thoughtworks.auctioncommissionservice.common.KeepingStatus;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        try {
            Delegation delegation = delegationRepository.findByLotId(orderId);
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
            if(e.getMessage().contains(ErrorCode.INSUFFICIENT_FEE.getValue())) {
                throw new InsufficientFeePaymentException();
            }
            if (e.getMessage().contains(ErrorCode.INCORRECT_ACCOUNT_INFO.getValue())) {
                throw new IncorrectInfoPaymentException();
            }
            throw new PaymentException();
        }
    }

    public Boolean sendEvaluationRequest(Long orderId) {
        Delegation byLotId = delegationRepository.findByLotId(orderId);
        if(!byLotId.getKeepingStatus().equals(KeepingStatus.KEEPING)) {
            throw new LotNotFoundException();
        }
        jmsSender.sendEvaluationRequest(orderId);
        return true;
    }
}
