package com.thoughtworks.auctioncommissionservice.controller.dto;

import com.thoughtworks.auctioncommissionservice.common.AccountType;
import com.thoughtworks.auctioncommissionservice.common.PaymentStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponseDto {
    private String accountNumber;
    private Double amount;
    private String accountName;
    private AccountType accountType;
    private PaymentStatus paymentStatus;
}
