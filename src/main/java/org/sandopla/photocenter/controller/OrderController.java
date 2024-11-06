package org.sandopla.photocenter.controller;

import org.sandopla.photocenter.model.*;
import org.sandopla.photocenter.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderWithDetails orderWithDetails,
                                         Authentication authentication) {
        try {
            Client client = (Client) authentication.getPrincipal();
            Order order = orderWithDetails.getOrder();
            order.setClient(client);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus(Order.OrderStatus.NEW);

            // Створюємо замовлення з деталями
            Order createdOrder = orderService.createOrder(order, orderWithDetails.getOrderDetails());
            return ResponseEntity.ok(createdOrder);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating order: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    // Додаємо анотацію для безпеки
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id, Authentication authentication) {
        try {
            Client client = (Client) authentication.getPrincipal();
            Order order = orderService.getOrderById(id);

            // Перевіряємо права доступу
            if (client.getRole() == Role.OWNER ||
                    (client.getRole() == Role.ADMIN &&
                            order.getBranch().getId().equals(client.getBranch().getId()))) {
                return ResponseEntity.ok(order);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/my")
    public ResponseEntity<List<Order>> getMyOrders(Authentication authentication) {
        Client client = (Client) authentication.getPrincipal();
        List<Order> orders = orderService.getOrdersByClient(client);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id,
                                             @RequestBody OrderWithDetails orderWithDetails,
                                             Authentication authentication) {
        Client client = (Client) authentication.getPrincipal();
        Order existingOrder = orderService.getOrderById(id);

        // Перевіряємо, чи це замовлення належить поточному користувачу
        if (!existingOrder.getClient().getId().equals(client.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Order updatedOrder = orderService.updateOrder(id, orderWithDetails.getOrder(),
                orderWithDetails.getOrderDetails());
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id, Authentication authentication) {
        Client client = (Client) authentication.getPrincipal();
        Order order = orderService.getOrderById(id);

        // Перевіряємо, чи це замовлення належить поточному користувачу
        if (!order.getClient().getId().equals(client.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        orderService.deleteOrder(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody OrderStatusUpdate statusUpdate,
            Authentication authentication) {
        try {
            Client client = (Client) authentication.getPrincipal();
            Order order = orderService.getOrderById(id);

            // Перевірка прав доступу
            if (client.getRole() != Role.OWNER &&
                    !order.getBranch().getId().equals(client.getBranch().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You don't have permission to update this order");
            }

            Order updatedOrder = orderService.updateOrderStatus(id, statusUpdate.getStatus());
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating order status: " + e.getMessage());
        }
    }


}

class OrderWithDetails {
    private Order order;
    private List<OrderDetail> orderDetails;

    // Getters and setters
    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }
}