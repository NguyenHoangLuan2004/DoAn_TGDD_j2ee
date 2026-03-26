package com.hutech.demo.repository;

import com.hutech.demo.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByProductIdOrderByIdDesc(Long productId);

    void deleteByOrderId(Long orderId);
}
