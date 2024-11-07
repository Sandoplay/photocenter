package org.sandopla.photocenter.dto;

import lombok.Data;
import org.sandopla.photocenter.model.Order;
import org.sandopla.photocenter.model.OrderDetail;

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

    @Data
    public static class OrderDetailResponse {
        private Long id;
        private Long productId;
        private String productName;
        private Long serviceId;
        private String serviceName;
        private Integer quantity;
        private BigDecimal price;
    }

    public static OrderResponse fromOrder(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());

        // Client info
        response.setClientId(order.getClient().getId());
        response.setClientName(order.getClient().getName());
        response.setClientEmail(order.getClient().getEmail());
        response.setClientPhone(order.getClient().getPhoneNumber());

        // Branch info
        response.setBranchId(order.getBranch().getId());
        response.setBranchName(order.getBranch().getName());

        // Processing Branch info
        response.setProcessingBranchId(order.getProcessingBranch().getId());
        response.setProcessingBranchName(order.getProcessingBranch().getName());

        response.setOrderDate(order.getOrderDate());
        response.setCompletionDate(order.getCompletionDate());
        response.setStatus(order.getStatus().name());
        response.setUrgent(order.isUrgent());
        response.setTotalCost(order.getTotalCost());

        // Convert OrderDetails
        response.setOrderDetails(order.getOrderDetails().stream()
                .map(detail -> {
                    OrderDetailResponse detailResponse = new OrderDetailResponse();
                    detailResponse.setId(detail.getId());
                    detailResponse.setQuantity(detail.getQuantity());
                    detailResponse.setPrice(detail.getPrice());

                    if (detail.getProduct() != null) {
                        detailResponse.setProductId(detail.getProduct().getId());
                        detailResponse.setProductName(detail.getProduct().getName());
                    }

                    if (detail.getService() != null) {
                        detailResponse.setServiceId(detail.getService().getId());
                        detailResponse.setServiceName(detail.getService().getName());
                    }

                    return detailResponse;
                })
                .collect(Collectors.toList()));
        return response;
    }
}