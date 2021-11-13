package com.thoughtworks.auctioncommissionservice.repository;

import com.thoughtworks.auctioncommissionservice.repository.entity.Delegation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class DelegationRepositoryTest {

    @Autowired
    DelegationRepository delegationRepository;

    @BeforeEach
    void setUp() {
        delegationRepository.deleteAll();
    }

    @Test //工序5
    void should_return_delegation_when_find_by_lot_id() {
        long lotId = 1L;
        double amount = 300.00;
        delegationRepository.save(Delegation.builder().lotId(lotId).amount(amount).build());

        Delegation byLotId = delegationRepository.findByLotId(lotId);

        assertThat(byLotId.getLotId()).isEqualTo(lotId);
        assertThat(byLotId.getAmount()).isEqualTo(amount);
    }

    @Test //工序5
    void should_save_delegation_into_db_when_call_save_method() {
        long lotId = 1L;
        long lotId2 = 2L;
        double amount = 300.00;
        delegationRepository.save(Delegation.builder().lotId(lotId).amount(amount).build());
        delegationRepository.save(Delegation.builder().lotId(lotId2).amount(amount).build());

        List<Delegation> allRecords = delegationRepository.findAll();

        assertThat(allRecords.size()).isEqualTo(2);
        assertThat(allRecords.get(0).getAmount()).isEqualTo(amount);
    }
}
