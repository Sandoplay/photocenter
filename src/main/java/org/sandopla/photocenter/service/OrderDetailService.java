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
    private final OrderDetailRepository orderDetailRepository;
    private final PriceCalculationService priceCalculationService;

    @Autowired
    public OrderDetailService(OrderDetailRepository orderDetailRepository,
                              PriceCalculationService priceCalculationService) {
        this.orderDetailRepository = orderDetailRepository;
        this.priceCalculationService = priceCalculationService;
    }

    public OrderDetail createOrderDetail(OrderDetail detail, BigDecimal volumeDiscount, boolean hasDiscountCard) {
        if (detail.getService() != null) {
            detail.setPrice(priceCalculationService.calculateDetailPrice(detail, volumeDiscount));
        }
        else {
            detail.setPrice(priceCalculationService.calculateDetailPrice(detail, volumeDiscount, hasDiscountCard));
        }
        return orderDetailRepository.save(detail);
    }



    public List<OrderDetail> getOrderDetailsByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

    public void deleteOrderDetail(Long id) {
        orderDetailRepository.deleteById(id);
    }
}