package com.hutech.demo.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "app_users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_app_users_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_app_users_phone", columnNames = "phone")
        }
)
public class AppUser {

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_USER = "USER";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    // Tạm để nullable=true cho an toàn nếu DB cũ chưa có cột này.
    // Khi sửa form đăng ký xong có thể đổi sang nullable=false.
    @Column(unique = true, length = 20)
    private String phone;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 20)
    private String role = ROLE_USER;

    @Column(nullable = false)
    private Integer points = 0;

    @Column(nullable = false)
    private Boolean enabled = true;

    // Dự phòng cho OTP app/authenticator sau này
    @Column(length = 128)
    private String otpSecret;

    public AppUser() {
    }

    public AppUser(String fullName, String email, String phone, String password, String role, Integer points, Boolean enabled, String otpSecret) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.points = points;
        this.enabled = enabled;
        this.otpSecret = otpSecret;
    }

    @PrePersist
    @PreUpdate
    public void normalizeData() {
        if (this.email != null) {
            this.email = this.email.trim().toLowerCase();
        }

        if (this.phone != null) {
            this.phone = this.phone.trim();
            if (this.phone.isBlank()) {
                this.phone = null;
            }
        }

        if (this.role == null || this.role.isBlank()) {
            this.role = ROLE_USER;
        } else {
            this.role = this.role.trim().toUpperCase();
        }

        if (this.points == null || this.points < 0) {
            this.points = 0;
        }

        if (this.enabled == null) {
            this.enabled = true;
        }
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

    public String getPhone() {
        return phone;
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

    public Boolean getEnabled() {
        return enabled == null ? true : enabled;
    }

    public String getOtpSecret() {
        return otpSecret;
    }

    public boolean isAdmin() {
        return ROLE_ADMIN.equalsIgnoreCase(role);
    }

    public boolean isManager() {
        return ROLE_MANAGER.equalsIgnoreCase(role);
    }

    public boolean isUser() {
        return ROLE_USER.equalsIgnoreCase(role);
    }

    public boolean hasRole(String role) {
        return this.role != null && this.role.equalsIgnoreCase(role);
    }

    public void addPoints(int amount) {
        if (amount > 0) {
            this.points = getPoints() + amount;
        }
    }

    public boolean deductPoints(int amount) {
        if (amount <= 0) {
            return false;
        }
        if (getPoints() < amount) {
            return false;
        }
        this.points = getPoints() - amount;
        return true;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName != null ? fullName.trim() : null;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setOtpSecret(String otpSecret) {
        this.otpSecret = otpSecret;
    }
}