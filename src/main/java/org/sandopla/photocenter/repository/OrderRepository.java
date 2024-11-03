package org.sandopla.photocenter.repository;

import org.sandopla.photocenter.model.Order;
import org.sandopla.photocenter.model.Client;
import org.sandopla.photocenter.model.Branch;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByClientOrderByOrderDateDesc(Client client);
    int countByBranchAndOrderDateBetween(Branch branch, LocalDateTime start, LocalDateTime end);
    List<Order> findByBranch(Branch branch);
    List<Order> findTop10ByOrderByOrderDateDesc();
    List<Order> findByBranchAndOrderDateBetween(Branch branch, LocalDateTime start, LocalDateTime end);
    //List<Order> findTopNByBranchOrderByOrderDateDesc(Branch branch, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.branch = :branch ORDER BY o.orderDate DESC")
    List<Order> findTopNByBranchOrderByOrderDateDesc(@Param("branch") Branch branch, Pageable pageable);
}