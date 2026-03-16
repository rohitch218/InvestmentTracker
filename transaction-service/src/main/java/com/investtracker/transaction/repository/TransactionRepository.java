package com.investtracker.transaction.repository;

import com.investtracker.transaction.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findAllByTenantIdAndUserId(String tenantId, Long userId, Pageable pageable);

    List<Transaction> findAllByInvestmentIdAndTenantId(Long investmentId, String tenantId);
}
