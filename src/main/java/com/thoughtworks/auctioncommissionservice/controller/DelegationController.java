package com.thoughtworks.auctioncommissionservice.controller;

import com.thoughtworks.auctioncommissionservice.controller.dto.AccountType;
import com.thoughtworks.auctioncommissionservice.controller.dto.PaymentDto;
import com.thoughtworks.auctioncommissionservice.controller.dto.PaymentResponseDto;
import com.thoughtworks.auctioncommissionservice.exception.BadRequestException;
import com.thoughtworks.auctioncommissionservice.service.DelegationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/delegation-orders/{oid}")
public class DelegationController {

    private final DelegationService delegationService;

    public DelegationController(DelegationService delegationService) {
        this.delegationService = delegationService;
    }

    @PostMapping("unsold-payment-request/confirmation")
    public ResponseEntity<PaymentResponseDto> makePaymentForUnsoldLot(@PathVariable("oid") Long orderId, @RequestBody PaymentDto paymentInfo) {
        return ResponseEntity.ok(delegationService.makePaymentForUnsoldLot(orderId, paymentInfo));
    }

    @PostMapping("evaluation-request")
    public void sendEvaluationRequest(@PathVariable("oid") Long orderId) {
        delegationService.sendEvaluationRequest(orderId);
    }
}
