package com.hutech.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "app_users")
public class AppUser {

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_CUSTOMER = "CUSTOMER";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role = ROLE_CUSTOMER;

    private Integer points = 0;

    public AppUser() {
    }

    public AppUser(String fullName, String email, String password, String role, Integer points) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.points = points;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public Integer getPoints() {
        return points == null ? 0 : points;
    }

    public boolean isAdmin() {
        return ROLE_ADMIN.equalsIgnoreCase(role);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}
