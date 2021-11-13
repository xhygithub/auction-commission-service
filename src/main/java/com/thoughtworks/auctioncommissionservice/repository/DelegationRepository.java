package com.thoughtworks.auctioncommissionservice.repository;

import com.thoughtworks.auctioncommissionservice.repository.entity.Delegation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DelegationRepository extends JpaRepository<Delegation, Long> {
    Delegation findByLotId(Long id);

    Delegation save(Delegation delegation);
}
