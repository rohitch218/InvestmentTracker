package com.investtracker.portfolio.repository;

import com.investtracker.portfolio.entity.Investment;
import com.investtracker.portfolio.entity.Investment.InvestmentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    @Query("""
        SELECT i FROM Investment i
        WHERE i.tenantId = :tenantId
          AND i.userId = :userId
          AND (:type IS NULL OR i.type = :type)
          AND (:search IS NULL 
               OR LOWER(i.name) LIKE LOWER(CONCAT('%', :search, '%'))
               OR LOWER(i.symbol) LIKE LOWER(CONCAT('%', :search, '%')))
        """)
    Page<Investment> findByTenantAndUser(
        @Param("tenantId") String tenantId,
        @Param("userId")   Long userId,
        @Param("type")     InvestmentType type,
        @Param("search")   String search,
        Pageable pageable
    );

    Page<Investment> findAllByTenantId(String tenantId, Pageable pageable);

    Optional<Investment> findByIdAndTenantIdAndUserId(Long id, String tenantId, Long userId);

    @Query("""
        SELECT i FROM Investment i
        WHERE i.tenantId = :tenantId AND i.userId = :userId
        ORDER BY (i.currentPrice - i.purchasePrice) / i.purchasePrice DESC
        """)
    List<Investment> findTopPerformers(
        @Param("tenantId") String tenantId,
        @Param("userId")   Long userId,
        Pageable pageable
    );

    @Query("""
        SELECT i.type as type, SUM(i.currentPrice * i.quantity) as totalValue
        FROM Investment i
        WHERE i.tenantId = :tenantId AND i.userId = :userId
        GROUP BY i.type
        """)
    List<Object[]> getAllocationByType(
        @Param("tenantId") String tenantId,
        @Param("userId")   Long userId
    );

    long countByTenantIdAndUserId(String tenantId, Long userId);
}
