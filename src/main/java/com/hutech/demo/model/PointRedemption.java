package com.hutech.demo.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "point_redemptions")
public class PointRedemption {

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_VERIFIED = "VERIFIED";
    public static final String STATUS_USED = "USED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private String voucherCode;

    @Column(nullable = false)
    private String voucherName;

    @Column(nullable = false)
    private Integer pointsUsed;

    @Column(nullable = false)
    private Double discountAmount;

    @Column(nullable = false)
    private String otpCode;

    @Column(nullable = false)
    private String status = STATUS_PENDING;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime verifiedAt;

    private LocalDateTime usedAt;

    private String usedOrderCode;

    public PointRedemption() {
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public String getVoucherName() {
        return voucherName;
    }

    public Integer getPointsUsed() {
        return pointsUsed == null ? 0 : pointsUsed;
    }

    public Double getDiscountAmount() {
        return discountAmount == null ? 0d : discountAmount;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public String getUsedOrderCode() {
        return usedOrderCode;
    }

    @Transient
    public boolean isVerified() {
        return STATUS_VERIFIED.equals(status);
    }

    @Transient
    public boolean isUsed() {
        return STATUS_USED.equals(status);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public void setVoucherName(String voucherName) {
        this.voucherName = voucherName;
    }

    public void setPointsUsed(Integer pointsUsed) {
        this.pointsUsed = pointsUsed;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }

    public void setUsedOrderCode(String usedOrderCode) {
        this.usedOrderCode = usedOrderCode;
    }
}
