package org.sandopla.photocenter.controller;

import org.sandopla.photocenter.dto.OrderResponse;
import org.sandopla.photocenter.model.*;
import org.sandopla.photocenter.service.BranchService;
import org.sandopla.photocenter.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final BranchService branchService;

    @Autowired
    public OrderController(OrderService orderService, BranchService branchService) {
        this.orderService = orderService;
        this.branchService = branchService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderWithDetails orderWithDetails,
                                         Authentication authentication) {
        try {
            Client client = (Client) authentication.getPrincipal();
            Order order = orderWithDetails.getOrder();

            // Отримуємо повний об'єкт Branch з бази даних
            Long branchId = order.getBranch().getId();
            Branch branch = branchService.getBranchById(branchId);
            order.setBranch(branch);

            order.setClient(client);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus(Order.OrderStatus.NEW);

            // Встановлюємо processingBranch на основі типу філії
            Branch orderBranch = order.getBranch();
            if (orderBranch.getType() == Branch.BranchType.KIOSK) {
                order.setProcessingBranch(orderBranch.getParentBranch());
            } else {
                order.setProcessingBranch(orderBranch);
            }


            // Створюємо замовлення з деталями
            Order createdOrder = orderService.createOrder(order, orderWithDetails.getOrderDetails());
            System.out.println("Order before serialization: " + createdOrder);
            return ResponseEntity.ok(OrderResponse.fromOrder(createdOrder));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating order: " + e.getMessage());
        }
    }



    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'USER')")
    public ResponseEntity<?> getOrderById(@PathVariable Long id, Authentication authentication) {
        try {
            Client client = (Client) authentication.getPrincipal();
            Order order = orderService.getOrderById(id);

            // Перевіряємо, чи це замовлення належить поточному користувачу
            if (!order.getClient().getId().equals(client.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Перевіряємо права доступу
            boolean hasAccess = false;

            if (client.getRole() == Role.OWNER) {
                hasAccess = true;
            } else if (client.getRole() == Role.ADMIN) {
                // Адміністратор має доступ до замовлень своєї філії та її кіосків
                Branch adminBranch = client.getBranch();
                hasAccess = order.getBranch().equals(adminBranch) ||
                        (order.getBranch().getParentBranch() != null &&
                                order.getBranch().getParentBranch().equals(adminBranch));
            } else {
                // Звичайний користувач має доступ тільки до своїх замовлень
                hasAccess = order.getClient().getId().equals(client.getId());
            }

            if (!hasAccess) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You don't have permission to view this order");
            }

            OrderResponse orderResponse = OrderResponse.fromOrder(order);
            return ResponseEntity.ok(orderResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving order: " + e.getMessage());
        }
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>> getMyOrders(Authentication authentication) {  // Змінено тип повернення
        Client client = (Client) authentication.getPrincipal();
        List<Order> orders = orderService.getOrdersByClient(client);
        // Конвертуємо список Order в список OrderResponse
        List<OrderResponse> responses = orders.stream()
                .map(OrderResponse::fromOrder)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id,  // Змінено тип повернення
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
        // Конвертуємо в OrderResponse
        return ResponseEntity.ok(OrderResponse.fromOrder(updatedOrder));
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

            // Перевіряємо права доступу
            boolean hasAccess = false;

            if (client.getRole() == Role.OWNER) {
                hasAccess = true;
            } else if (client.getRole() == Role.ADMIN) {
                Branch adminBranch = client.getBranch();
                // Адміністратор має доступ до замовлень своєї філії та її кіосків
                if (order.getBranch().equals(adminBranch)) {
                    hasAccess = true;
                } else if (order.getBranch().getParentBranch() != null) {
                    hasAccess = order.getBranch().getParentBranch().equals(adminBranch);
                }

                // Додатково перевіряємо processingBranch
                if (order.getProcessingBranch().equals(adminBranch)) {
                    hasAccess = true;
                }
            }

            if (!hasAccess) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You don't have permission to update this order");
            }

            // Додаткова перевірка коректності зміни статусу
            if (!isStatusTransitionAllowed(order.getStatus(), statusUpdate.getStatus())) {
                return ResponseEntity.badRequest()
                        .body("Invalid status transition from " + order.getStatus() +
                                " to " + statusUpdate.getStatus());
            }

            Order updatedOrder = orderService.updateOrderStatus(id, statusUpdate.getStatus());
            return ResponseEntity.ok(OrderResponse.fromOrder(updatedOrder));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating order status: " + e.getMessage());
        }
    }

    // Допоміжний метод для перевірки коректності зміни статусу
    private boolean isStatusTransitionAllowed(Order.OrderStatus currentStatus,
                                              Order.OrderStatus newStatus) {
        if (currentStatus == null || newStatus == null) {
            return false;
        }

        switch (currentStatus) {
            case NEW:
                return newStatus == Order.OrderStatus.IN_PROGRESS ||
                        newStatus == Order.OrderStatus.CANCELLED;
            case IN_PROGRESS:
                return newStatus == Order.OrderStatus.COMPLETED ||
                        newStatus == Order.OrderStatus.CANCELLED;
            case COMPLETED:
                return false; // Завершене замовлення не можна змінювати
            case CANCELLED:
                return false; // Скасоване замовлення не можна змінювати
            default:
                return false;
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