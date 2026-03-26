package com.hutech.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "reward_vouchers")
public class RewardVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer pointsRequired;

    @Column(nullable = false)
    private Double discountAmount;

    @Column(nullable = false)
    private Double minOrderAmount = 0d;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Boolean reusable = false;

    @Column(length = 500)
    private String description;

    public RewardVoucher() {
    }

    public RewardVoucher(String code, String name, Integer pointsRequired, Double discountAmount, String description, Boolean active) {
        this.code = code;
        this.name = name;
        this.pointsRequired = pointsRequired;
        this.discountAmount = discountAmount;
        this.description = description;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Integer getPointsRequired() {
        return pointsRequired == null ? 0 : pointsRequired;
    }

    public Double getDiscountAmount() {
        return discountAmount == null ? 0d : discountAmount;
    }

    public Double getMinOrderAmount() {
        return minOrderAmount == null ? 0d : minOrderAmount;
    }

    public Boolean getActive() {
        return active == null ? Boolean.FALSE : active;
    }

    public Boolean getReusable() {
        return reusable == null ? Boolean.FALSE : reusable;
    }

    public String getDescription() {
        return description;
    }

    @Transient
    public boolean isUsableFor(double subtotal) {
        return getActive() && subtotal >= getMinOrderAmount();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPointsRequired(Integer pointsRequired) {
        this.pointsRequired = pointsRequired;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public void setMinOrderAmount(Double minOrderAmount) {
        this.minOrderAmount = minOrderAmount == null ? 0d : Math.max(minOrderAmount, 0d);
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setReusable(Boolean reusable) {
        this.reusable = reusable;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
