package org.sandopla.photocenter.service;

import org.sandopla.photocenter.model.Order;
import org.sandopla.photocenter.model.OrderDetail;
import org.sandopla.photocenter.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        Order savedOrder = orderRepository.save(order);
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

    @Transactional
    public Order updateOrder(Long id, Order orderDetails, List<OrderDetail> newOrderDetails) {
        Order order = getOrderById(id);
        // Update order fields
        order.setClient(orderDetails.getClient());
        order.setBranch(orderDetails.getBranch());
        order.setOrderDate(orderDetails.getOrderDate());
        order.setCompletionDate(orderDetails.getCompletionDate());
        order.setStatus(orderDetails.getStatus());
        order.setUrgent(orderDetails.isUrgent());

        // Update order details
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
}