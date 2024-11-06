package org.sandopla.photocenter.service;

import org.sandopla.photocenter.model.*;
import org.sandopla.photocenter.repository.OrderRepository;
import org.sandopla.photocenter.repository.specifications.OrderSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailService orderDetailService;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderDetailService orderDetailService) {
        this.orderRepository = orderRepository;
        this.orderDetailService = orderDetailService;
    }

    @Transactional
    public Order createOrder(Order order, List<OrderDetail> orderDetails) {
        order.setTotalCost(BigDecimal.ZERO);
        order.setOrderDetails(new ArrayList<>());
        Order savedOrder = orderRepository.save(order);

        BigDecimal totalCost = BigDecimal.ZERO;

        for (OrderDetail detail : orderDetails) {
            try {
                detail.setOrder(savedOrder);

                // Перевіряємо і конвертуємо значення
                if (detail.getPrice() == null) {
                    throw new IllegalArgumentException("Price is required");
                }

                // Явно конвертуємо в BigDecimal
                detail.setPrice(new BigDecimal(detail.getPrice().toString()));

                OrderDetail savedDetail = orderDetailService.createOrderDetail(detail);
                savedOrder.getOrderDetails().add(savedDetail);

                if (savedDetail.getPrice() != null) {
                    totalCost = totalCost.add(savedDetail.getPrice());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error processing order detail: " + e.getMessage());
            }
        }

        savedOrder.setTotalCost(totalCost);
        return orderRepository.save(savedOrder);
    }

    private void validateOrderDetail(OrderDetail detail) {
        if (detail.getProduct() == null && detail.getService() == null) {
            throw new IllegalArgumentException("OrderDetail must have either product or service");
        }
        if (detail.getQuantity() == null || detail.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersByClient(Client client) {
        return orderRepository.findByClientOrderByOrderDateDesc(client);
    }

    @Transactional
    public Order updateOrder(Long id, Order orderDetails, List<OrderDetail> newOrderDetails) {
        Order order = getOrderById(id);

        // Оновлюємо основні дані замовлення
        order.setBranch(orderDetails.getBranch());
        order.setOrderDate(orderDetails.getOrderDate());
        order.setCompletionDate(orderDetails.getCompletionDate());
        order.setStatus(orderDetails.getStatus());
        order.setUrgent(orderDetails.isUrgent());

        // Оновлюємо деталі замовлення
        List<OrderDetail> existingDetails = orderDetailService.getOrderDetailsByOrderId(id);
        existingDetails.forEach(detail -> orderDetailService.deleteOrderDetail(detail.getId()));

        for (OrderDetail detail : newOrderDetails) {
            detail.setOrder(order);
            orderDetailService.createOrderDetail(detail);
        }

        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    public BigDecimal getTotalRevenue() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(Order::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getTodayOrdersCount(Branch branch) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return orderRepository.countByBranchAndOrderDateBetween(branch, startOfDay, endOfDay);
    }

    public BigDecimal getBranchRevenue(Branch branch) {
        List<Order> branchOrders = orderRepository.findByBranch(branch);
        return branchOrders.stream()
                .map(Order::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Order> getLastOrders(int count) {
        return orderRepository.findTop10ByOrderByOrderDateDesc();
    }

    public List<Order> getBranchOrdersByDate(Branch branch, LocalDateTime start, LocalDateTime end) {
        return orderRepository.findByBranchAndOrderDateBetween(branch, start, end);
    }

    public List<Order> getRecentOrdersByClient(Client client, int limit) {
        return orderRepository.findByClientOrderByOrderDateDesc(client)
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    public List<Order> getLastBranchOrders(Branch branch, int count) {
        return orderRepository.findTopNByBranchOrderByOrderDateDesc(
                branch,
                PageRequest.of(0, count)
        );
    }

    public List<Order> getLastOrdersForBranch(Branch branch, int count) {
        return orderRepository.findTopNByBranchOrderByOrderDateDesc(
                branch,
                PageRequest.of(0, count)
        );
    }

    public List<Order> findAllWithFilters(OrderFilter filter, Pageable pageable) {
        Specification<Order> spec = buildSpecification(filter);
        return orderRepository.findAll(spec, pageable).getContent();
    }

    public List<Order> findByBranchWithFilters(Branch branch, OrderFilter filter, Pageable pageable) {
        Specification<Order> spec = Specification.where(OrderSpecifications.belongsToBranch(branch));
        spec = spec.and(buildSpecification(filter));
        return orderRepository.findAll(spec, pageable).getContent();
    }

    private Specification<Order> buildSpecification(OrderFilter filter) {
        Specification<Order> spec = Specification.where(null);

        if (filter.getStatus() != null) {
            spec = spec.and(OrderSpecifications.hasStatus(filter.getStatus()));
        }

        if (filter.getUrgent() != null) {
            spec = spec.and(OrderSpecifications.isUrgent(filter.getUrgent()));
        }

        if (filter.getSearch() != null && !filter.getSearch().isEmpty()) {
            spec = spec.and(OrderSpecifications.matchesSearch(filter.getSearch()));
        }

        return spec;
    }
}