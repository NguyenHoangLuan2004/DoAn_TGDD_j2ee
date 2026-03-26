package com.hutech.demo.repository;

import com.hutech.demo.model.RewardVoucher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RewardVoucherRepository extends JpaRepository<RewardVoucher, Long> {
    List<RewardVoucher> findByActiveTrueOrderByPointsRequiredAsc();

    List<RewardVoucher> findByActiveTrueAndMinOrderAmountLessThanEqualOrderByPointsRequiredAsc(Double subtotal);

    Optional<RewardVoucher> findByCodeIgnoreCase(String code);
}
