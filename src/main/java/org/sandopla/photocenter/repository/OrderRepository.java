package org.sandopla.photocenter.repository;

import org.sandopla.photocenter.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Специфічні методи запитів
}