package org.sandopla.photocenter.repository;

import org.sandopla.photocenter.model.Order;
import org.sandopla.photocenter.model.Client;
import org.sandopla.photocenter.model.Branch;
import org.sandopla.photocenter.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    List<Order> findByClientOrderByOrderDateDesc(Client client);
    int countByBranchAndOrderDateBetween(Branch branch, LocalDateTime start, LocalDateTime end);
    List<Order> findByBranch(Branch branch);
    List<Order> findTop10ByOrderByOrderDateDesc();
    List<Order> findByBranchAndOrderDateBetween(Branch branch, LocalDateTime start, LocalDateTime end);
    //List<Order> findTopNByBranchOrderByOrderDateDesc(Branch branch, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.branch = :branch ORDER BY o.orderDate DESC")
    List<Order> findTopNByBranchOrderByOrderDateDesc(@Param("branch") Branch branch, Pageable pageable);

    @Query("SELECT SUM(o.totalCost) FROM Order o WHERE o.orderDate BETWEEN :start AND :end")
    BigDecimal sumTotalCostByOrderDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(o.totalCost) FROM Order o WHERE o.branch = :branch AND o.orderDate BETWEEN :start AND :end")
    BigDecimal sumTotalCostByBranchAndOrderDateBetween(Branch branch, LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(o.totalCost) FROM Order o WHERE o.branch = :branch AND o.isUrgent = :isUrgent AND o.orderDate BETWEEN :start AND :end")
    BigDecimal sumTotalCostByBranchAndIsUrgentAndOrderDateBetween(Branch branch, boolean isUrgent, LocalDateTime start, LocalDateTime end);

    // Додаємо новий метод для отримання замовлень з кіосків
    @Query("SELECT o FROM Order o WHERE o.branch IN " +
            "(SELECT b FROM Branch b WHERE b.parentBranch = :branch) " +
            "OR o.branch = :branch " +
            "ORDER BY o.orderDate DESC")
    List<Order> findByBranchAndKiosks(@Param("branch") Branch branch);

    // Метод для отримання замовлень з кіосків з фільтрацією за датою
    @Query("SELECT o FROM Order o WHERE (o.branch IN " +
            "(SELECT b FROM Branch b WHERE b.parentBranch = :branch) " +
            "OR o.branch = :branch) " +
            "AND o.orderDate BETWEEN :start AND :end " +
            "ORDER BY o.orderDate DESC")
    List<Order> findByBranchAndKiosksAndDateBetween(
            @Param("branch") Branch branch,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    // Метод для підрахунку кількості замовлень для філії та її кіосків
    @Query("SELECT COUNT(o) FROM Order o WHERE (o.branch IN " +
            "(SELECT b FROM Branch b WHERE b.parentBranch = :branch) " +
            "OR o.branch = :branch) " +
            "AND o.orderDate BETWEEN :start AND :end")
    int countByBranchAndKiosksAndDateBetween(
            @Param("branch") Branch branch,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    // Метод для отримання виручки філії разом з кіосками
    @Query("SELECT SUM(o.totalCost) FROM Order o WHERE (o.branch IN " +
            "(SELECT b FROM Branch b WHERE b.parentBranch = :branch) " +
            "OR o.branch = :branch) " +
            "AND o.orderDate BETWEEN :start AND :end")
    BigDecimal sumTotalCostByBranchAndKiosksAndDateBetween(
            @Param("branch") Branch branch,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);


    @Query("SELECT COUNT(o) FROM Order o WHERE o.client = :client AND o.branch = :branch")
    int countByClientAndBranch(@Param("client") Client client, @Param("branch") Branch branch);

    @Query("SELECT COUNT(o) > 0 FROM Order o " +
            "JOIN o.orderDetails od " +
            "WHERE od.product = :product " +
            "AND o.branch = :branch " +
            "AND o.client = :client")
    boolean existsByProductAndBranch(@Param("product") Product product,
                                     @Param("branch") Branch branch);
}