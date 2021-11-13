package com.thoughtworks.auctioncommissionservice.repository.entity;

import com.thoughtworks.auctioncommissionservice.common.AuctionStatus;
import com.thoughtworks.auctioncommissionservice.common.KeepingStatus;
import com.thoughtworks.auctioncommissionservice.common.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Delegation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long lotId;
    private Double amount; //尾款
    private PaymentStatus paymentStatus; //支付状态
    private AuctionStatus auctionStatus; //sold pr unsold
    private KeepingStatus keepingStatus; //sold pr unsold
    private Long payTime;
}
