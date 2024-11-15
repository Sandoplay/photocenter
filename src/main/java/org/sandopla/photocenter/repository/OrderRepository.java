package org.sandopla.photocenter.repository;

import org.sandopla.photocenter.dto.*;
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


    @Query("""
    SELECT NEW org.sandopla.photocenter.dto.PhotoStatisticsDTO(
        b.id,
        b.name,
        SUM(CASE 
            WHEN o.isUrgent = false 
            AND NOT EXISTS (
                SELECT 1 
                FROM OrderDetail frameDetail
                WHERE frameDetail.order = o
                AND frameDetail.product.category = 'Frames'
            )
            THEN od.quantity 
            ELSE 0 
        END),
        SUM(CASE 
            WHEN o.isUrgent = true 
            AND NOT EXISTS (
                SELECT 1 
                FROM OrderDetail frameDetail
                WHERE frameDetail.order = o
                AND frameDetail.product.category = 'Frames'
            )
            THEN od.quantity 
            ELSE 0 
        END),
        SUM(CASE 
            WHEN EXISTS (
                SELECT 1 
                FROM OrderDetail frameDetail
                WHERE frameDetail.order = o
                AND frameDetail.product.category = 'Frames'
            )
            AND od.service IS NOT NULL 
            THEN od.quantity 
            ELSE 0 
        END)
    )
    FROM Order o
    JOIN o.branch b
    JOIN o.orderDetails od
    WHERE o.orderDate BETWEEN :startDate AND :endDate
    AND (:branchId IS NULL OR b.id = :branchId)
    AND od.service IS NOT NULL
    GROUP BY b.id, b.name
""")
    List<PhotoStatisticsDTO> getPhotoStatistics(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("branchId") Long branchId
    );

    @Query("""
        SELECT NEW org.sandopla.photocenter.dto.ServiceOrdersStatisticsDTO(
            b.id,
            b.name,
            s.name,
            SUM(CASE WHEN o.isUrgent = false THEN 1 ELSE 0 END),
            SUM(CASE WHEN o.isUrgent = true THEN 1 ELSE 0 END),
            COUNT(o)
        )
        FROM Order o
        JOIN o.branch b
        JOIN o.orderDetails od
        JOIN od.service s
        WHERE o.orderDate BETWEEN :startDate AND :endDate
        AND (:branchId IS NULL OR b.id = :branchId)
        GROUP BY b.id, b.name, s.name
        ORDER BY b.name, s.name
        """)
    List<ServiceOrdersStatisticsDTO> getServiceOrdersStatistics(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("branchId") Long branchId
    );


    @Query("""
    SELECT NEW org.sandopla.photocenter.dto.BranchOrdersStatisticsDTO(
        b.id,
        b.name,
        b.type,
        pb.id,
        pb.name,
        COUNT(o)
    )
    FROM Order o
    JOIN o.branch b
    LEFT JOIN b.parentBranch pb
    WHERE o.orderDate BETWEEN :startDate AND :endDate
    GROUP BY b.id, b.name, b.type, pb.id, pb.name
    ORDER BY b.type, 
        CASE WHEN b.type = 'MAIN_OFFICE' THEN 0
             WHEN b.type = 'BRANCH' THEN 1
             ELSE 2 END,
        b.name
""")
    List<BranchOrdersStatisticsDTO> getBranchOrdersStatistics(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COUNT(DISTINCT o.client.id) FROM Order o WHERE o.branch = :branch AND o.orderDate BETWEEN :startDate AND :endDate AND o.totalCost >= 500")
    long countClientsWithLargeOrders(@Param("branch") Branch branch,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(DISTINCT o.client.id) FROM Order o WHERE o.branch IS NULL AND o.orderDate BETWEEN :startDate AND :endDate AND o.totalCost >= 500")
    long countClientsWithLargeOrders(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(DISTINCT o.client.id) FROM Order o WHERE o.branch.id = :branchId AND o.totalCost >= 500 AND o.orderDate BETWEEN :startDate AND :endDate")
    long countClientsWithLargeOrdersByBranch(@Param("branchId") Long branchId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT NEW org.sandopla.photocenter.dto.ClientStatisticsDTO(" +
            "b.name, " +
            "c.name, " +
            "c.phoneNumber, " +
            "c.email, " +
            "SUM(o.totalCost), " +
            "COUNT(o), " +
            "CASE WHEN COUNT(dc) > 0 THEN true ELSE false END) " +
            "FROM Order o " +
            "JOIN o.client c " +
            "JOIN o.branch b " +
            "LEFT JOIN DiscountCard dc ON dc.client = c AND dc.active = true " +
            "WHERE o.orderDate BETWEEN :startDate AND :endDate " +
            "AND (:branchId IS NULL OR b.id = :branchId) " +
            "AND (:minOrderAmount IS NULL OR o.totalCost >= :minOrderAmount) " +
            "AND (:hasDiscount IS NULL OR " +
            "   (:hasDiscount = true AND EXISTS (SELECT 1 FROM DiscountCard d WHERE d.client = c AND d.active = true)) OR " +
            "   (:hasDiscount = false AND NOT EXISTS (SELECT 1 FROM DiscountCard d WHERE d.client = c AND d.active = true))) " +
            "GROUP BY b.name, c.name, c.phoneNumber, c.email " +
            "ORDER BY b.name, c.name")
    List<ClientStatisticsDTO> getClientStatistics(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("branchId") Long branchId,
            @Param("minOrderAmount") BigDecimal minOrderAmount,
            @Param("hasDiscount") Boolean hasDiscount
    );

    @Query("""
    SELECT NEW org.sandopla.photocenter.dto.UnclaimedOrderDTO(
        o.id,
        b.name,
        CASE 
            WHEN b.type = 'MAIN_OFFICE' THEN 'Головний офіс'
            WHEN b.type = 'BRANCH' THEN 'Філія'
            ELSE 'Кіоск'
        END,
        pb.name,
        c.name,
        c.phoneNumber,
        o.orderDate,
        o.completionDate,
        o.totalCost,
        o.status
    )
    FROM Order o 
    JOIN o.branch b
    JOIN o.client c
    LEFT JOIN b.parentBranch pb
    WHERE o.pickedUp = false 
    AND o.status = 'COMPLETED'
    AND (:branchId IS NULL OR b.id = :branchId)
    ORDER BY b.type, b.name, o.completionDate DESC
""")
    List<UnclaimedOrderDTO> findUnclaimedOrders(@Param("branchId") Long branchId);

    @Query("""
    SELECT NEW org.sandopla.photocenter.dto.ProductSalesDTO(
        p.id,
        p.name,
        p.category,
        SUM(od.quantity),
        SUM(od.price),
        b.name
    )
    FROM Order o
    JOIN o.branch b 
    JOIN o.orderDetails od
    JOIN od.product p
    WHERE o.orderDate BETWEEN :startDate AND :endDate
    AND (:branchId IS NULL OR b.id = :branchId)
    GROUP BY p.id, p.name, p.category, b.name
    ORDER BY b.name, p.category, p.name
""")
    List<ProductSalesDTO> getProductSales(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("branchId") Long branchId
    );

    @Query("""
    SELECT NEW org.sandopla.photocenter.dto.SalesRevenueDto(
        b.name,
        CAST(SUM(od.price * od.quantity) AS double),
        CAST(SUM(sp.supplierPrice * od.quantity) AS double)
    )
    FROM Order o
    JOIN o.branch b
    JOIN o.orderDetails od
    JOIN od.product p
    JOIN SupplierProduct sp ON sp.product = p
    WHERE o.orderDate BETWEEN :startDate AND :endDate
    AND (:branchId IS NULL OR b.id = :branchId)
    AND o.status = 'COMPLETED'
    GROUP BY b.name
    ORDER BY b.name
""")
    List<SalesRevenueDto> getSalesRevenue(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("branchId") Long branchId
    );

    @Query("""
    SELECT NEW org.sandopla.photocenter.dto.ServiceRevenueDTO(
        b.id,
        b.name,
        s.name,
        CAST(SUM(CASE WHEN o.isUrgent = false THEN od.price * od.quantity ELSE 0 END) AS double),
        CAST(SUM(CASE WHEN o.isUrgent = true THEN od.price * od.quantity ELSE 0 END) AS double),
        CAST(SUM(od.price * od.quantity) AS double)
    )
    FROM Order o
    JOIN o.branch b
    JOIN o.orderDetails od
    JOIN od.service s
    WHERE o.orderDate BETWEEN :startDate AND :endDate
    AND (:branchId IS NULL OR b.id = :branchId OR b.parentBranch.id = :branchId)
    AND o.status = 'COMPLETED'
    GROUP BY b.id, b.name, s.name
    ORDER BY b.name, s.name
""")
    List<ServiceRevenueDTO> getServiceRevenue(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("branchId") Long branchId
    );

}

