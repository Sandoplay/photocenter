package org.sandopla.photocenter.service;

import org.sandopla.photocenter.model.OrderDetail;
import org.sandopla.photocenter.model.Product;
import org.sandopla.photocenter.repository.OrderDetailRepository;
import org.sandopla.photocenter.repository.ProductRepository;
import org.sandopla.photocenter.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderDetailService {

    private static final BigDecimal PRODUCT_URGENT_MULTIPLIER = new BigDecimal("1.10"); // +10%

    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final ServiceRepository serviceRepository;

    @Autowired
    public OrderDetailService(OrderDetailRepository orderDetailRepository,
                              ProductRepository productRepository,
                              ServiceRepository serviceRepository) {
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
        this.serviceRepository = serviceRepository;
    }

    public OrderDetail createOrderDetail(OrderDetail orderDetail) {
        if (orderDetail.getQuantity() == null || orderDetail.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        BigDecimal basePrice;
        BigDecimal urgentMultiplier = null;

        // Отримуємо актуальну ціну з бази даних
        if (orderDetail.getProduct() != null) {
            Product product = productRepository.findById(orderDetail.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            basePrice = product.getPrice();
            urgentMultiplier = PRODUCT_URGENT_MULTIPLIER;
            orderDetail.setProduct(product);
        } else if (orderDetail.getService() != null) {
            org.sandopla.photocenter.model.Service service = serviceRepository.findById(orderDetail.getService().getId())
                    .orElseThrow(() -> new RuntimeException("Service not found"));
            basePrice = service.getBasePrice();
            urgentMultiplier = service.getUrgentPriceMultiplier();
            orderDetail.setService(service);
        } else {
            throw new IllegalArgumentException("Either product or service must be specified");
        }

        // Спочатку множимо базову ціну на кількість
        BigDecimal quantity = new BigDecimal(orderDetail.getQuantity());
        BigDecimal totalPrice = basePrice.multiply(quantity);

        // Потім застосовуємо множник терміновості якщо потрібно
        if (orderDetail.getOrder().isUrgent() && urgentMultiplier != null) {
            totalPrice = totalPrice.multiply(urgentMultiplier);
        }

        // Логуємо розрахунки для відладки
        System.out.println("Price calculation:");
        System.out.println("Base price: " + basePrice);
        System.out.println("Quantity: " + quantity);
        System.out.println("Price after quantity: " + totalPrice);
        System.out.println("Is urgent: " + orderDetail.getOrder().isUrgent());
        System.out.println("Urgent multiplier: " + urgentMultiplier);
        System.out.println("Final price: " + totalPrice);

        // Округляємо до 2 знаків після коми
        totalPrice = totalPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
        orderDetail.setPrice(totalPrice);

        return orderDetailRepository.save(orderDetail);
    }

    public List<OrderDetail> getOrderDetailsByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

    public void deleteOrderDetail(Long id) {
        orderDetailRepository.deleteById(id);
    }
}