package org.sandopla.photocenter.repository.specifications;

import org.sandopla.photocenter.model.Branch;
import org.sandopla.photocenter.model.Order;
import org.springframework.data.jpa.domain.Specification;

public class OrderSpecifications {
    public static Specification<Order> belongsToBranch(Branch branch) {
        return (root, query, cb) -> cb.equal(root.get("branch"), branch);
    }

    public static Specification<Order> hasStatus(Order.OrderStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Order> isUrgent(boolean urgent) {
        return (root, query, cb) -> cb.equal(root.get("isUrgent"), urgent);
    }

    public static Specification<Order> matchesSearch(String search) {
        return (root, query, cb) -> {
            String pattern = "%" + search.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("id").as(String.class)), pattern),
                    cb.like(cb.lower(root.get("client").get("name")), pattern),
                    cb.like(cb.lower(root.get("branch").get("name")), pattern)
            );
        };
    }
}