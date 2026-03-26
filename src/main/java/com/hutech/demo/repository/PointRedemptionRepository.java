package com.hutech.demo.repository;

import com.hutech.demo.model.PointRedemption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PointRedemptionRepository extends JpaRepository<PointRedemption, Long> {
    List<PointRedemption> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<PointRedemption> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, String status);

    Optional<PointRedemption> findTopByUserIdAndVoucherCodeOrderByCreatedAtDesc(Long userId, String voucherCode);

    Optional<PointRedemption> findTopByUserIdAndVoucherCodeAndStatusOrderByCreatedAtDesc(Long userId, String voucherCode, String status);

    boolean existsByUserIdAndVoucherCodeAndStatus(Long userId, String voucherCode, String status);
}
