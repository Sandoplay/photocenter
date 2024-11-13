package org.sandopla.photocenter.repository;

import org.sandopla.photocenter.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountCardRepository extends JpaRepository<DiscountCard, Long> {
    @Query("SELECT dc FROM DiscountCard dc WHERE dc.client = :client " +
            "AND dc.active = true " +
            "AND (dc.expiryDate IS NULL OR dc.expiryDate >= CURRENT_DATE)")
    Optional<DiscountCard> findActiveCardByClient(@Param("client") Client client);
    Optional<DiscountCard> findByCardNumber(String cardNumber);
    Optional<DiscountCard> findByClientAndActive(Client client, boolean active);
    boolean existsByCardNumber(String cardNumber);
    List<DiscountCard> findByClient(Client client);

}
