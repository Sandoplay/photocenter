package org.sandopla.photocenter.repository;

import org.sandopla.photocenter.model.Branch;
import org.sandopla.photocenter.model.Client;
import org.sandopla.photocenter.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByRole(Role role);
    Optional<Client> findByEmail(String email);

    @Query("SELECT c FROM Client c LEFT JOIN FETCH c.branch WHERE c.username = :username")
    Optional<Client> findByUsername(@Param("username") String username);

    @Query("SELECT c FROM Client c LEFT JOIN FETCH c.branch WHERE c.id = :id")
    Optional<Client> findById(@Param("id") Long id);

    List<Client> findByRoleAndBranch(Role role, Branch branch);

    @Query("SELECT COUNT(c) FROM Client c WHERE c.branch = :branch")
    long countByBranch(@Param("branch") Branch branch);

//    @Query("SELECT COUNT(DISTINCT c.id) FROM Client c LEFT JOIN c.discountCards dc WHERE dc.active = true AND (dc.expiryDate IS NULL OR dc.expiryDate > CURRENT_DATE) AND (:branchId IS NULL OR c.branch.id = :branchId)")
//    long countClientsWithDiscountCards(@Param("branchId") Long branchId);

    @Query("SELECT COUNT(DISTINCT c.id) FROM Client c WHERE (:branchId IS NULL OR c.branch.id = :branchId) AND EXISTS (SELECT dc FROM DiscountCard dc WHERE dc.client.id = c.id AND dc.active = true AND (dc.expiryDate IS NULL OR dc.expiryDate > CURRENT_DATE))")
    long getClientsWithDiscountsByBranch(@Param("branchId") Long branchId);

    @Query("SELECT COUNT(DISTINCT c.id) FROM Client c WHERE EXISTS (SELECT dc FROM DiscountCard dc WHERE dc.client.id = c.id AND dc.active = true AND (dc.expiryDate IS NULL OR dc.expiryDate > CURRENT_DATE))")
    long countClientsWithDiscounts();

    @Query("SELECT COUNT(DISTINCT c.id) FROM Client c WHERE c.branch.id = :branchId AND EXISTS (SELECT dc FROM DiscountCard dc WHERE dc.client.id = c.id AND dc.active = true AND (dc.expiryDate IS NULL OR dc.expiryDate > CURRENT_DATE))")
    long countClientsWithDiscountsByBranch(@Param("branchId") Long branchId);

    @Query("SELECT COUNT(DISTINCT c) FROM Client c WHERE c.branch = :branch")
    Long countClientsByBranch(@Param("branch") Branch branch);

    @Query("""
        SELECT COUNT(DISTINCT c) FROM Client c 
        WHERE (:branch IS NULL OR c.branch = :branch)
        AND EXISTS (
            SELECT 1 FROM DiscountCard dc 
            WHERE dc.client = c 
            AND dc.active = true 
            AND (dc.expiryDate IS NULL OR dc.expiryDate > CURRENT_DATE)
        )
    """)
    Long countClientsWithDiscountsByBranch(@Param("branch") Branch branch);

    @Query("""
       SELECT COUNT(DISTINCT c) FROM Client c 
       JOIN Order o ON o.client = c
       JOIN Branch b ON c.branch = b
       WHERE (:branch IS NULL OR c.branch = :branch)
       AND o.orderDate BETWEEN :startDate AND :endDate
       GROUP BY c.id
       HAVING SUM(o.totalCost) >= :minOrderAmount
   """)
    Long countClientsWithLargeOrdersByBranch(
            Branch branch,
            LocalDateTime startDate,
            LocalDateTime endDate,
            BigDecimal minOrderAmount
    );

}