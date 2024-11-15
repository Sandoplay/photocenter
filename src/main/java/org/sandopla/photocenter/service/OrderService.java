package org.sandopla.photocenter.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Subquery;
import lombok.extern.slf4j.Slf4j;
import org.sandopla.photocenter.dto.SalesRevenueDto;
import org.sandopla.photocenter.dto.UnclaimedOrderDTO;
import org.sandopla.photocenter.model.*;
import org.sandopla.photocenter.repository.DiscountCardRepository;
import org.sandopla.photocenter.repository.OrderRepository;
import org.sandopla.photocenter.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailService orderDetailService;
    private final DiscountCardService discountCardService;
    private final DiscountCardRepository discountCardRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        OrderDetailService orderDetailService,
                        DiscountCardService discountCardService,
                        DiscountCardRepository discountCardRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailService = orderDetailService;
        this.discountCardService = discountCardService;
        this.discountCardRepository = discountCardRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order createOrder(Order order, List<OrderDetail> orderDetails) {
        log.debug("Starting to create order: {}", order);
        log.debug("Order details: {}", orderDetails);

        try {
            order.setOrderDetails(new ArrayList<>());
            log.debug("Saving initial order");
            Order savedOrder = orderRepository.save(order);
            log.debug("Initial order saved with ID: {}", savedOrder.getId());

            int totalPhotos = orderDetails.stream()
                    .filter(detail -> detail.getService() != null)
                    .mapToInt(OrderDetail::getQuantity)
                    .sum();
            log.debug("Total photos calculated: {}", totalPhotos);

            BigDecimal volumeDiscount = calculateVolumeDiscount(totalPhotos);
            log.debug("Volume discount calculated: {}", volumeDiscount);

            BigDecimal totalCost = BigDecimal.ZERO;

            log.debug("Checking discount card for client: {}", order.getClient());
            boolean hasDiscountCard = discountCardService.getClientDiscount(order.getClient()).isPresent();
            List<DiscountCard> discountCards = discountCardRepository.findByClient(order.getClient());
            log.debug("Found discount cards: {}", discountCards);

            if (!discountCards.isEmpty()) {
                DiscountCard card = discountCards.get(0);
                log.debug("First discount card: {}, active: {}", card, card.isActive());
                hasDiscountCard = card.isActive();
            } else {
                log.debug("No discount cards found");
                hasDiscountCard = false;
            }

            for (OrderDetail detail : orderDetails) {
                log.debug("Processing order detail: {}", detail);

                if (detail.getProduct() != null) {
                    log.debug("Processing product with ID: {}", detail.getProduct().getId());
                    Product product = productRepository.findById(detail.getProduct().getId())
                            .orElseThrow(() -> {
                                log.error("Product not found with ID: {}", detail.getProduct().getId());
                                return new RuntimeException("Product not found with id: " + detail.getProduct().getId());
                            });

                    int currentStock = product.getStockQuantity();
                    int requestedQuantity = detail.getQuantity();
                    log.debug("Current stock: {}, Requested quantity: {}", currentStock, requestedQuantity);

                    if (currentStock < requestedQuantity) {
                        log.error("Not enough stock for product: {}", product.getName());
                        throw new RuntimeException("Not enough stock for product: " + product.getName());
                    }

                    product.setStockQuantity(currentStock - requestedQuantity);
                    productRepository.save(product);
                    detail.setProduct(product);
                }

                detail.setOrder(savedOrder);
                log.debug("Creating order detail with volume discount: {} and discount card: {}",
                        volumeDiscount, hasDiscountCard);
                OrderDetail savedDetail = orderDetailService.createOrderDetail(detail, volumeDiscount, hasDiscountCard);
                savedOrder.getOrderDetails().add(savedDetail);
                totalCost = totalCost.add(savedDetail.getPrice());
                log.debug("Current total cost: {}", totalCost);
            }

            if (order.isUrgent()) {
                log.debug("Order is urgent, doubling total cost");
                totalCost = totalCost.multiply(new BigDecimal("2"));
            }

            savedOrder.setTotalCost(totalCost);
            log.debug("Final total cost: {}", totalCost);

            Order finalOrder = orderRepository.save(savedOrder);
            log.debug("Order successfully saved: {}", finalOrder);
            return finalOrder;

        } catch (Exception e) {
            log.error("Error creating order", e);
            throw new RuntimeException("Error creating order: " + e.getMessage(), e);
        }
    }

    private BigDecimal calculateVolumeDiscount(int quantity) {
        if (quantity >= 50) return new BigDecimal("0.20");      // 20% знижка за 50+ одиниць
        if (quantity >= 30) return new BigDecimal("0.15");      // 15% знижка за 30+ одиниць
        if (quantity >= 20) return new BigDecimal("0.10");      // 10% знижка за 20+ одиниць
        if (quantity >= 10) return new BigDecimal("0.05");      // 5% знижка за 10+ одиниць
        return BigDecimal.ZERO;
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

        // Рахуємо загальну кількість фотографій для знижки
        int totalPhotos = newOrderDetails.stream()
                .filter(detail -> detail.getService() != null)
                .mapToInt(OrderDetail::getQuantity)
                .sum();

        BigDecimal volumeDiscount = calculateVolumeDiscount(totalPhotos);

        // Видаляємо старі деталі
        List<OrderDetail> existingDetails = orderDetailService.getOrderDetailsByOrderId(id);
        existingDetails.forEach(detail -> orderDetailService.deleteOrderDetail(detail.getId()));

        // Додаємо нові деталі
        BigDecimal totalCost = BigDecimal.ZERO;
        boolean hasDiscountCard = discountCardService.getClientDiscount(order.getClient()).isPresent();
        for (OrderDetail detail : newOrderDetails) {
            detail.setOrder(order);
            OrderDetail savedDetail = orderDetailService.createOrderDetail(detail, volumeDiscount, hasDiscountCard);
            totalCost = totalCost.add(savedDetail.getPrice());
        }

        // Оновлюємо загальну вартість
        if (order.isUrgent()) {
            totalCost = totalCost.multiply(new BigDecimal("2"));
        }
        order.setTotalCost(totalCost);

        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    public SalesRevenueDto getTotalRevenue() {
        LocalDateTime startOfYear = LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0);
        LocalDateTime now = LocalDateTime.now();

        List<SalesRevenueDto> revenues = orderRepository.getSalesRevenue(startOfYear, now, null);

        if (revenues.isEmpty()) {
            return new SalesRevenueDto("Total", 0.0, 0.0);
        }

        BigDecimal totalRevenue = revenues.stream()
                .map(SalesRevenueDto::getTotalRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCost = revenues.stream()
                .map(SalesRevenueDto::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new SalesRevenueDto("Total",
                totalRevenue.doubleValue(),
                totalCost.doubleValue());  // Створюємо новий об'єкт з актуальними даними
    }

    public SalesRevenueDto getBranchRevenue(Branch branch) {
        LocalDateTime startOfYear = LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0);
        LocalDateTime now = LocalDateTime.now();

        return orderRepository.getSalesRevenue(startOfYear, now, branch.getId())
                .stream()
                .findFirst()
                .map(dto -> new SalesRevenueDto(
                        branch.getName(),
                        dto.getTotalRevenue().doubleValue(),
                        dto.getTotalCost().doubleValue()))
                .orElse(new SalesRevenueDto(branch.getName(), 0.0, 0.0));
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
//    public BigDecimal getBranchRevenue(Branch branch, LocalDateTime start, LocalDateTime end) {
//        return orderRepository.sumTotalCostByBranchAndOrderDateBetween(branch, start, end);
//    }

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
//    public BigDecimal getBranchRevenue(Branch branch) {
//        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1)
//                .withHour(0).withMinute(0).withSecond(0);
//        LocalDateTime now = LocalDateTime.now();
//
//        if (branch.getType() == Branch.BranchType.BRANCH ||
//                branch.getType() == Branch.BranchType.MAIN_OFFICE) {
//            BigDecimal revenue = orderRepository.sumTotalCostByBranchAndKiosksAndDateBetween(
//                    branch, monthStart, now);
//            return revenue != null ? revenue : BigDecimal.ZERO;
//        }
//        BigDecimal revenue = orderRepository.sumTotalCostByBranchAndOrderDateBetween(
//                branch, monthStart, now);
//        return revenue != null ? revenue : BigDecimal.ZERO;
//    }

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

    public List<UnclaimedOrderDTO> getUnclaimedOrders(Long branchId) {
        return orderRepository.findUnclaimedOrders(branchId);
    }

    @Transactional
    public Order pickupOrder(Long orderId, String clientUsername) throws AccessDeniedException {
        Order order = getOrderById(orderId);

        // Перевіряємо чи замовлення належить клієнту
        if (!order.getClient().getUsername().equals(clientUsername)) {
            throw new AccessDeniedException("You don't have permission to pickup this order");
        }

        // Перевіряємо чи замовлення завершене
        if (order.getStatus() != Order.OrderStatus.COMPLETED) {
            throw new IllegalStateException("Order is not ready for pickup");
        }

        // Перевіряємо чи замовлення ще не забране
        if (order.isPickedUp()) {
            throw new IllegalStateException("Order is already picked up");
        }

        order.setPickedUp(true);
        order.setPickupDate(LocalDateTime.now());

        return orderRepository.save(order);
    }
}