package org.sandopla.photocenter.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Subquery;
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
    private final DiscountService discountService;


    @Autowired
    public OrderService(OrderRepository orderRepository, OrderDetailService orderDetailService, DiscountService discountService) {
        this.orderRepository = orderRepository;
        this.orderDetailService = orderDetailService;
        this.discountService = discountService;

    }

    @Transactional
    public Order createOrder(Order order, List<OrderDetail> orderDetails) {
        // Перевіряємо обмеження для термінових замовлень
        if (order.isUrgent() && order.getBranch().getType() == Branch.BranchType.KIOSK) {
            throw new IllegalStateException("Термінові замовлення приймаються тільки у філіях");
        }

        // Зберігаємо замовлення
        order.setOrderDetails(new ArrayList<>());
        order.setTotalCost(BigDecimal.ZERO);
        Order savedOrder = orderRepository.save(order);

        // Розраховуємо загальну вартість з урахуванням знижок
        BigDecimal totalCost = BigDecimal.ZERO;

        for (OrderDetail detail : orderDetails) {
            detail.setOrder(savedOrder);

            // Перевіряємо безкоштовну проявку плівки
            if (isFilmDevelopmentService(detail) &&
                    discountService.isFilmDevelopmentFree(detail.getProduct(), order.getBranch())) {
                detail.setPrice(BigDecimal.ZERO);
            }

            OrderDetail savedDetail = orderDetailService.createOrderDetail(detail);
            savedOrder.getOrderDetails().add(savedDetail);

            totalCost = totalCost.add(savedDetail.getPrice());
        }

        // Застосовуємо знижки
        BigDecimal discountPercentage = discountService.calculateTotalDiscount(savedOrder);
        BigDecimal discount = totalCost.multiply(discountPercentage);

        savedOrder.setTotalCost(totalCost.subtract(discount));
        return orderRepository.save(savedOrder);
    }

    private boolean isFilmDevelopmentService(OrderDetail detail) {
        if (detail.getService() == null || detail.getService().getName() == null) {
            return false;
        }

        if (detail.getProduct() == null || detail.getProduct().getCategory() == null) {
            return false;
        }

        return detail.getService().getName().toLowerCase().contains("проявка") &&
                detail.getProduct().getCategory().equals("FILM");
    }

    private void validateOrderDetail(OrderDetail detail) {
        if (detail.getProduct() == null && detail.getService() == null) {
            throw new IllegalArgumentException("OrderDetail must have either product or service");
        }
        if (detail.getQuantity() == null || detail.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));

        // Примусово завантажуємо всі необхідні дані
        order.getClient().getName(); // Завантажуємо дані клієнта
        order.getBranch().getName(); // Завантажуємо дані філії
        order.getProcessingBranch().getName(); // Завантажуємо дані філії обробки

        // Завантажуємо деталі замовлення
        order.getOrderDetails().forEach(detail -> {
            if (detail.getProduct() != null) {
                detail.getProduct().getName();
            }
            if (detail.getService() != null) {
                detail.getService().getName();
            }
        });

        return order;
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

    public List<Order> getLastOrders(int count) {
        return orderRepository.findTop10ByOrderByOrderDateDesc();
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        Order order = getOrderById(orderId);

        if (newStatus == Order.OrderStatus.COMPLETED) {
            order.setCompletionDate(LocalDateTime.now());
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    // Загальна виручка
    public BigDecimal getTotalRevenue(LocalDateTime start, LocalDateTime end) {
        return orderRepository.sumTotalCostByOrderDateBetween(start, end);
    }

    // Виручка по філії
    public BigDecimal getBranchRevenue(Branch branch, LocalDateTime start, LocalDateTime end) {
        return orderRepository.sumTotalCostByBranchAndOrderDateBetween(branch, start, end);
    }

    // Виручка по типу замовлення (термінові/звичайні)
    public BigDecimal getRevenueByUrgency(Branch branch, boolean isUrgent, LocalDateTime start, LocalDateTime end) {
        return orderRepository.sumTotalCostByBranchAndIsUrgentAndOrderDateBetween(
                branch, isUrgent, start, end);
    }

    // Оновлюємо метод отримання замовлень для філії
    public List<Order> getBranchOrdersByDate(Branch branch, LocalDateTime start, LocalDateTime end) {
        // Якщо це філія, отримуємо замовлення з неї та її кіосків
        if (branch.getType() == Branch.BranchType.BRANCH ||
                branch.getType() == Branch.BranchType.MAIN_OFFICE) {
            return orderRepository.findByBranchAndKiosksAndDateBetween(branch, start, end);
        }
        // Якщо це кіоск, повертаємо тільки його замовлення
        return orderRepository.findByBranchAndOrderDateBetween(branch, start, end);
    }

    // Оновлюємо метод підрахунку замовлень
    public int getTodayOrdersCount(Branch branch) {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        if (branch.getType() == Branch.BranchType.BRANCH ||
                branch.getType() == Branch.BranchType.MAIN_OFFICE) {
            return orderRepository.countByBranchAndKiosksAndDateBetween(branch, startOfDay, endOfDay);
        }
        return orderRepository.countByBranchAndOrderDateBetween(branch, startOfDay, endOfDay);
    }

    // Оновлюємо метод отримання виручки
    public BigDecimal getBranchRevenue(Branch branch) {
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1)
                .withHour(0).withMinute(0).withSecond(0);
        LocalDateTime now = LocalDateTime.now();

        if (branch.getType() == Branch.BranchType.BRANCH ||
                branch.getType() == Branch.BranchType.MAIN_OFFICE) {
            BigDecimal revenue = orderRepository.sumTotalCostByBranchAndKiosksAndDateBetween(
                    branch, monthStart, now);
            return revenue != null ? revenue : BigDecimal.ZERO;
        }
        BigDecimal revenue = orderRepository.sumTotalCostByBranchAndOrderDateBetween(
                branch, monthStart, now);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    // Оновлюємо метод отримання останніх замовлень
    public List<Order> getLastOrdersForBranch(Branch branch, int count) {
        if (branch.getType() == Branch.BranchType.BRANCH ||
                branch.getType() == Branch.BranchType.MAIN_OFFICE) {
            List<Order> orders = orderRepository.findByBranchAndKiosks(branch);
            return orders.stream().limit(count).toList();
        }
        return orderRepository.findTopNByBranchOrderByOrderDateDesc(branch,
                org.springframework.data.domain.PageRequest.of(0, count));
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

    public List<Order> findAllWithFilters(OrderFilter filter, Pageable pageable) {
        Specification<Order> spec = buildSpecification(filter);
        return orderRepository.findAll(spec, pageable).getContent();
    }

    public List<Order> findByBranchWithFilters(Branch branch, OrderFilter filter, Pageable pageable) {
        // Створюємо базову специфікацію для філії та її кіосків
        Specification<Order> branchSpec = null;

        if (branch.getType() == Branch.BranchType.BRANCH ||
                branch.getType() == Branch.BranchType.MAIN_OFFICE) {
            // Для філії: замовлення філії ТА всіх її кіосків
            branchSpec = (root, query, cb) -> {
                query.distinct(true); // Забезпечуємо унікальність результатів

                // Створюємо підзапит для отримання ID кіосків
                Subquery<Long> kioskSubquery = query.subquery(Long.class);
                var kioskRoot = kioskSubquery.from(Branch.class);
                kioskSubquery.select(kioskRoot.get("id"))
                        .where(cb.equal(kioskRoot.get("parentBranch"), branch));

                // Повертаємо умову: замовлення з філії АБО з кіосків
                return cb.or(
                        cb.equal(root.get("branch"), branch),
                        root.get("branch").get("id").in(kioskSubquery)
                );
            };
        } else {
            // Для кіоска: тільки його замовлення
            branchSpec = (root, query, cb) -> cb.equal(root.get("branch"), branch);
        }

        // Додаємо інші фільтри
        Specification<Order> filterSpec = buildSpecification(filter);

        // Комбінуємо специфікації
        Specification<Order> finalSpec = branchSpec.and(filterSpec);

        return orderRepository.findAll(finalSpec, pageable).getContent();
    }

    private Specification<Order> buildSpecification(OrderFilter filter) {
        Specification<Order> spec = Specification.where(null);

        if (filter.getStatus() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("status"), filter.getStatus()));
        }

        if (filter.getUrgent() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("isUrgent"), filter.getUrgent()));
        }

        if (filter.getSearch() != null && !filter.getSearch().trim().isEmpty()) {
            String searchPattern = "%" + filter.getSearch().toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("id").as(String.class)), searchPattern),
                            cb.like(cb.lower(root.get("client").get("name")), searchPattern),
                            cb.like(cb.lower(root.get("branch").get("name")), searchPattern)
                    ));
        }

        return spec;
    }
}