package com.hutech.demo.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer_orders")
public class CustomerOrder {

    public static final String PAYMENT_METHOD_COD = "COD";
    public static final String PAYMENT_METHOD_VNPAY = "VNPAY";
    public static final String PAYMENT_STATUS_UNPAID = "CHUA_THANH_TOAN";
    public static final String PAYMENT_STATUS_PENDING = "DANG_CHO_THANH_TOAN";
    public static final String PAYMENT_STATUS_PAID = "DA_THANH_TOAN";
    public static final String PAYMENT_STATUS_FAILED = "THAT_BAI";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderCode;

    private String status = "Đã tiếp nhận";

    private String fullName;

    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(columnDefinition = "TEXT")
    private String note;

    private double subtotal;

    private double shippingFee;

    private double discountAmount;

    private double voucherDiscount;

    private int pointsUsed;

    private int pointsEarned;

    private double grandTotal;

    private Integer totalQuantity = 0;

    private String voucherCode;

    private String voucherName;

    private String paymentMethod = PAYMENT_METHOD_COD;

    private String paymentStatus = PAYMENT_STATUS_UNPAID;

    private String paymentTransactionNo;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime paidAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public CustomerOrder() {
    }

    public Long getId() {
        return id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public String getStatus() {
        return status;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getNote() {
        return note;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getVoucherDiscount() {
        return voucherDiscount;
    }

    public int getPointsUsed() {
        return pointsUsed;
    }

    public int getPointsEarned() {
        return pointsEarned;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public Integer getTotalQuantity() {
        return totalQuantity == null ? 0 : totalQuantity;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public String getVoucherName() {
        return voucherName;
    }

    public String getPaymentMethod() {
        return paymentMethod == null || paymentMethod.isBlank() ? PAYMENT_METHOD_COD : paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus == null || paymentStatus.isBlank() ? PAYMENT_STATUS_UNPAID : paymentStatus;
    }

    public String getPaymentTransactionNo() {
        return paymentTransactionNo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public AppUser getUser() {
        return user;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    @Transient
    public double getPointsDiscountAmount() {
        return Math.max(discountAmount - voucherDiscount, 0);
    }

    @Transient
    public boolean isPaid() {
        return PAYMENT_STATUS_PAID.equalsIgnoreCase(getPaymentStatus());
    }

    @Transient
    public boolean isCod() {
        return PAYMENT_METHOD_COD.equalsIgnoreCase(getPaymentMethod());
    }

    @Transient
    public boolean isVnpay() {
        return PAYMENT_METHOD_VNPAY.equalsIgnoreCase(getPaymentMethod());
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = Math.max(subtotal, 0);
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = Math.max(shippingFee, 0);
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = Math.max(discountAmount, 0);
    }

    public void setVoucherDiscount(double voucherDiscount) {
        this.voucherDiscount = Math.max(voucherDiscount, 0);
    }

    public void setPointsUsed(int pointsUsed) {
        this.pointsUsed = Math.max(pointsUsed, 0);
    }

    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = Math.max(pointsEarned, 0);
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = Math.max(grandTotal, 0);
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity == null ? 0 : Math.max(totalQuantity, 0);
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public void setVoucherName(String voucherName) {
        this.voucherName = voucherName;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setPaymentTransactionNo(String paymentTransactionNo) {
        this.paymentTransactionNo = paymentTransactionNo;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
        this.totalQuantity = items == null ? 0 : items.stream().mapToInt(OrderItem::getQuantity).sum();
    }
}
