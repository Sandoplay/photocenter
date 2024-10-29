package org.sandopla.photocenter.service;

import org.sandopla.photocenter.model.Order;
import org.sandopla.photocenter.model.OrderDetail;
import org.sandopla.photocenter.model.Client;
import org.sandopla.photocenter.model.Branch;
import org.sandopla.photocenter.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
        // Зберігаємо замовлення
        Order savedOrder = orderRepository.save(order);

        // Зберігаємо деталі замовлення
        for (OrderDetail detail : orderDetails) {
            detail.setOrder(savedOrder);
            orderDetailService.createOrderDetail(detail);
        }

        return savedOrder;
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
}