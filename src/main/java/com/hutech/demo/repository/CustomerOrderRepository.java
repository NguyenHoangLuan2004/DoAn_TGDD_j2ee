package com.hutech.demo.repository;

import com.hutech.demo.model.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
    Optional<CustomerOrder> findByOrderCode(String orderCode);

    List<CustomerOrder> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<CustomerOrder> findByPaymentStatusOrderByCreatedAtDesc(String paymentStatus);

    List<CustomerOrder> findTop10ByOrderByCreatedAtDesc();
}
