package org.sandopla.photocenter.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
@EntityListeners(OrderListener.class)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "total_cost", nullable = false)
    private BigDecimal totalCost;

    @Column(name = "is_urgent")
    private boolean isUrgent;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;

    public enum OrderStatus {
        NEW, IN_PROGRESS, COMPLETED, CANCELLED
    }
}