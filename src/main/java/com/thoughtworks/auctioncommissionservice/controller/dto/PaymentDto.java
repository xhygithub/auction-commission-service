package com.thoughtworks.auctioncommissionservice.controller.dto;

import com.thoughtworks.auctioncommissionservice.common.AccountType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentDto {
    private String accountNumber;
    private Double amount;
    private String accountName;
    private AccountType accountType;
}
