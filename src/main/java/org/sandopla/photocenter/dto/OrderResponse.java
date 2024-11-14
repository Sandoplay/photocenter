package org.sandopla.photocenter.dto;

import lombok.Data;
import org.sandopla.photocenter.model.DiscountCard;
import org.sandopla.photocenter.model.Order;
import org.sandopla.photocenter.model.OrderDetail;
import org.sandopla.photocenter.repository.DiscountCardRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrderResponse {
    private Long id;
    private Long clientId;
    private String clientName;
    private String clientEmail;
    private String clientPhone;
    private Long branchId;
    private String branchName;
    private Long processingBranchId;
    private String processingBranchName;
    private LocalDateTime orderDate;
    private LocalDateTime completionDate;
    private String status;
    private boolean isUrgent;
    private BigDecimal totalCost;
    private List<OrderDetailResponse> orderDetails;

    public boolean getHasDiscountCard() {
        return this.orderDetails.stream()
                .filter(detail -> detail.productName != null)
                .anyMatch(detail -> {
                    BigDecimal originalPrice = detail.price.multiply(new BigDecimal("1.11111"));
                    return detail.price.compareTo(originalPrice) < 0;
                });
    }

    @Data
    public static class OrderDetailResponse {
        private Long id;
        private Long productId;
        private String productName;
        private Long serviceId;
        private String serviceName;
        private Integer quantity;
        private BigDecimal price;

        public static OrderDetailResponse fromOrderDetail(OrderDetail orderDetail) {
            OrderDetailResponse response = new OrderDetailResponse();
            response.setId(orderDetail.getId());
            response.setQuantity(orderDetail.getQuantity());
            response.setPrice(orderDetail.getPrice());

            if (orderDetail.getProduct() != null) {
                response.setProductId(orderDetail.getProduct().getId());
                response.setProductName(orderDetail.getProduct().getName());
            }

            if (orderDetail.getService() != null) {
                response.setServiceId(orderDetail.getService().getId());
                response.setServiceName(orderDetail.getService().getName());
            }

            return response;
        }
    }

    public static OrderResponse fromOrder(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setClientId(order.getClient().getId());
        response.setClientName(order.getClient().getName());
        response.setClientEmail(order.getClient().getEmail());
        response.setClientPhone(order.getClient().getPhoneNumber());
        response.setBranchId(order.getBranch().getId());
        response.setBranchName(order.getBranch().getName());
        response.setProcessingBranchId(order.getProcessingBranch().getId());
        response.setProcessingBranchName(order.getProcessingBranch().getName());
        response.setOrderDate(order.getOrderDate());
        response.setCompletionDate(order.getCompletionDate());
        response.setStatus(order.getStatus().name());
        response.setUrgent(order.isUrgent());
        response.setTotalCost(order.getTotalCost());

        response.setOrderDetails(order.getOrderDetails().stream()
                .map(OrderDetailResponse::fromOrderDetail)
                .collect(Collectors.toList()));

        return response;
    }
}