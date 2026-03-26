package com.hutech.demo.service;

import com.hutech.demo.model.AppUser;
import com.hutech.demo.model.PointRedemption;
import com.hutech.demo.model.RewardVoucher;
import com.hutech.demo.repository.AppUserRepository;
import com.hutech.demo.repository.PointRedemptionRepository;
import com.hutech.demo.repository.RewardVoucherRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class RewardService {

    private final RewardVoucherRepository rewardVoucherRepository;
    private final PointRedemptionRepository pointRedemptionRepository;
    private final AppUserRepository appUserRepository;

    public RewardService(RewardVoucherRepository rewardVoucherRepository,
                         PointRedemptionRepository pointRedemptionRepository,
                         AppUserRepository appUserRepository) {
        this.rewardVoucherRepository = rewardVoucherRepository;
        this.pointRedemptionRepository = pointRedemptionRepository;
        this.appUserRepository = appUserRepository;
    }

    public List<RewardVoucher> getActiveVouchers() {
        return rewardVoucherRepository.findByActiveTrueOrderByPointsRequiredAsc();
    }

    public List<PointRedemption> getHistory(Long userId) {
        return pointRedemptionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public PointRedemption getById(Long id) {
        return pointRedemptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu đổi điểm"));
    }

    public PointRedemption startRedemption(AppUser user, Long voucherId) {
        RewardVoucher voucher = rewardVoucherRepository.findById(voucherId)
                .filter(RewardVoucher::getActive)
                .orElseThrow(() -> new IllegalArgumentException("Voucher không hợp lệ"));

        if (user.getPoints() < voucher.getPointsRequired()) {
            throw new IllegalArgumentException("Bạn không đủ điểm để đổi voucher này");
        }

        PointRedemption redemption = new PointRedemption();
        redemption.setUserId(user.getId());
        redemption.setUserEmail(user.getEmail());
        redemption.setVoucherCode(voucher.getCode());
        redemption.setVoucherName(voucher.getName());
        redemption.setPointsUsed(voucher.getPointsRequired());
        redemption.setDiscountAmount(voucher.getDiscountAmount());
        redemption.setOtpCode(generateOtp());
        redemption.setStatus(PointRedemption.STATUS_PENDING);
        redemption.setCreatedAt(LocalDateTime.now());

        return pointRedemptionRepository.save(redemption);
    }

    public boolean verifyOtp(AppUser user, Long redemptionId, String otp) {
        PointRedemption redemption = getById(redemptionId);

        if (!redemption.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("Bạn không có quyền xác thực yêu cầu này");
        }

        if (PointRedemption.STATUS_VERIFIED.equals(redemption.getStatus())) {
            return true;
        }

        if (!PointRedemption.STATUS_PENDING.equals(redemption.getStatus())) {
            throw new IllegalArgumentException("Yêu cầu đổi điểm không còn hợp lệ");
        }

        if (redemption.getCreatedAt() != null
                && redemption.getCreatedAt().plusMinutes(5).isBefore(LocalDateTime.now())) {
            redemption.setStatus(PointRedemption.STATUS_CANCELLED);
            pointRedemptionRepository.save(redemption);
            throw new IllegalArgumentException("Mã OTP đã hết hạn, vui lòng đổi lại");
        }

        if (otp == null || !otp.trim().equals(redemption.getOtpCode())) {
            return false;
        }

        AppUser dbUser = appUserRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản"));

        if (dbUser.getPoints() < redemption.getPointsUsed()) {
            throw new IllegalArgumentException("Điểm hiện tại không đủ để hoàn tất đổi thưởng");
        }

        dbUser.setPoints(dbUser.getPoints() - redemption.getPointsUsed());
        appUserRepository.save(dbUser);

        redemption.setStatus(PointRedemption.STATUS_VERIFIED);
        redemption.setVerifiedAt(LocalDateTime.now());
        pointRedemptionRepository.save(redemption);

        return true;
    }

    public PointRedemption getVerifiedRedemptionForCheckout(AppUser user, Long redemptionId) {
        PointRedemption redemption = getById(redemptionId);

        if (!redemption.getUserId().equals(user.getId())) {
            throw new IllegalArgumentException("Voucher không thuộc về tài khoản hiện tại");
        }

        if (!PointRedemption.STATUS_VERIFIED.equals(redemption.getStatus())) {
            throw new IllegalArgumentException("Voucher chưa được xác thực để thanh toán");
        }

        return redemption;
    }

    public void markVoucherUsed(Long redemptionId, String orderCode) {
        PointRedemption redemption = getById(redemptionId);
        redemption.setStatus(PointRedemption.STATUS_USED);
        redemption.setUsedAt(LocalDateTime.now());
        redemption.setUsedOrderCode(orderCode);
        pointRedemptionRepository.save(redemption);
    }

    private String generateOtp() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }
}
