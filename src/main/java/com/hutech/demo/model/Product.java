package com.hutech.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên sản phẩm là bắt buộc")
    @Column(nullable = false)
    private String name;

    @DecimalMin(value = "0.0", inclusive = false, message = "Giá bán phải lớn hơn 0")
    private double price;

    private Double originalPrice;

    @Column(columnDefinition = "TEXT")
    private String shortDescription;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String image;

    private String brand;

    private Integer stock = 10;

    private boolean featured = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @NotNull(message = "Danh mục là bắt buộc")
    private Category category = new Category();

    public Product() {
    }

    public Product(String name, double price, Double originalPrice, String shortDescription,
                   String description, String image, String brand, Integer stock,
                   boolean featured, Category category) {
        this.name = name;
        this.price = price;
        this.originalPrice = originalPrice;
        this.shortDescription = shortDescription;
        this.description = description;
        this.image = image;
        this.brand = brand;
        this.stock = stock;
        this.featured = featured;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getBrand() {
        return brand;
    }

    public Integer getStock() {
        return stock == null ? 0 : stock;
    }

    public boolean isFeatured() {
        return featured;
    }

    public Category getCategory() {
        return category;
    }

    public int getDiscountPercent() {
        if (originalPrice == null || originalPrice <= 0 || originalPrice <= price) {
            return 0;
        }
        return (int) Math.round((1 - (price / originalPrice)) * 100);
    }

    public String getFormattedBrand() {
        return brand == null ? "" : brand;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
