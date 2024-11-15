package org.sandopla.photocenter.service;

import org.sandopla.photocenter.dto.*;
import org.sandopla.photocenter.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatisticsService {
    private final OrderRepository orderRepository;

    public StatisticsService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<PhotoStatisticsDTO> getPhotoStatistics(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Long branchId) {

        return orderRepository.getPhotoStatistics(startDate, endDate, branchId);
    }

    public PhotoStatisticsDTO getTotalPhotoStatistics(
            LocalDateTime startDate,
            LocalDateTime endDate) {

        List<PhotoStatisticsDTO> statistics =
                orderRepository.getPhotoStatistics(startDate, endDate, null);

        // Об'єднуємо статистику по всіх філіях
        return statistics.stream()
                .reduce(new PhotoStatisticsDTO(), (total, stat) -> {
                    total.setPhotosWithFramesCount(
                            total.getPhotosWithFramesCount() + stat.getPhotosWithFramesCount());
                    total.setRegularPhotosCount(
                            total.getRegularPhotosCount() + stat.getRegularPhotosCount());
                    total.setUrgentPhotosCount(
                            total.getUrgentPhotosCount() + stat.getUrgentPhotosCount());
                    return total;
                });
    }

    public List<ServiceOrdersStatisticsDTO> getServiceOrdersStatistics(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Long branchId) {
        return orderRepository.getServiceOrdersStatistics(startDate, endDate, branchId);
    }

    public List<BranchOrdersStatisticsDTO> getBranchOrdersStatistics(
            LocalDateTime startDate,
            LocalDateTime endDate) {
        return orderRepository.getBranchOrdersStatistics(startDate, endDate);
    }

    public List<UnclaimedOrderDTO> getUnclaimedOrders(Long branchId) {
        return orderRepository.findUnclaimedOrders(branchId);
    }

    public List<ServiceRevenueDTO> getServiceRevenue(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Long branchId) {
        return orderRepository.getServiceRevenue(startDate, endDate, branchId);
    }

    public List<ProductOrdersStatisticsDTO> getProductOrdersStatistics(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Long branchId) {
        return orderRepository.getProductOrdersStatistics(startDate, endDate, branchId);
    }


}