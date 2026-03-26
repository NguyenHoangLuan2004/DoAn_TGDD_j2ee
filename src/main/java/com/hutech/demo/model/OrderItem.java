package com.hutech.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private String productName;

    private String productImage;

    private String productBrand;

    private double unitPrice;

    private int quantity;

    private double lineTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private CustomerOrder order;

    public OrderItem() {
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getLineTotal() {
        return lineTotal;
    }

    public CustomerOrder getOrder() {
        return order;
    }

    @Transient
    public boolean hasProductReference() {
        return productId != null && productId > 0;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = Math.max(unitPrice, 0);
    }

    public void setQuantity(int quantity) {
        this.quantity = Math.max(quantity, 0);
    }

    public void setLineTotal(double lineTotal) {
        this.lineTotal = Math.max(lineTotal, 0);
    }

    public void setOrder(CustomerOrder order) {
        this.order = order;
    }
}
