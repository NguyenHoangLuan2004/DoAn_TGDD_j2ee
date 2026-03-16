package com.hutech.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên danh mục không được để trống")
    @Column(nullable = false, unique = true)
    private String name;

    private String iconClass;

    private Integer displayOrder = 0;

    public Category() {
    }

    public Category(String name, String iconClass, Integer displayOrder) {
        this.name = name;
        this.iconClass = iconClass;
        this.displayOrder = displayOrder;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIconClass() {
        return iconClass;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
