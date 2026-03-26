package com.hutech.demo.service;

import com.hutech.demo.model.AppUser;
import com.hutech.demo.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.default-role:USER}")
    private String defaultRole;

    public AuthService(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<AppUser> login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return Optional.empty();
        }

        String normalizedEmail = normalizeEmail(email);

        return appUserRepository.findByEmailIgnoreCase(normalizedEmail)
                .filter(user -> Boolean.TRUE.equals(user.getEnabled()))
                .filter(user -> passwordEncoder.matches(password, user.getPassword()));
    }

    public Optional<AppUser> getById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return appUserRepository.findById(id);
    }

    public Optional<AppUser> getByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }
        return appUserRepository.findByEmailIgnoreCase(normalizeEmail(email));
    }

    public Optional<AppUser> getByPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return Optional.empty();
        }
        return appUserRepository.findByPhone(normalizePhone(phone));
    }

    public boolean emailExists(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return appUserRepository.existsByEmailIgnoreCase(normalizeEmail(email));
    }

    public boolean phoneExists(String phone) {
        if (phone == null || phone.isBlank()) {
            return false;
        }
        return appUserRepository.existsByPhone(normalizePhone(phone));
    }

    public AppUser register(String fullName, String email, String phone, String rawPassword) {
        String normalizedEmail = normalizeEmail(email);
        String normalizedPhone = normalizePhone(phone);

        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Họ tên không được để trống");
        }

        if (normalizedEmail == null || normalizedEmail.isBlank()) {
            throw new IllegalArgumentException("Email không được để trống");
        }

        if (normalizedPhone == null || normalizedPhone.isBlank()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }

        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }

        if (appUserRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        if (appUserRepository.existsByPhone(normalizedPhone)) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại");
        }

        AppUser user = new AppUser();
        user.setFullName(fullName.trim());
        user.setEmail(normalizedEmail);
        user.setPhone(normalizedPhone);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(defaultRole != null && !defaultRole.isBlank()
                ? defaultRole.trim().toUpperCase()
                : AppUser.ROLE_USER);
        user.setPoints(0);
        user.setEnabled(true);

        return appUserRepository.save(user);
    }

    public AppUser save(AppUser user) {
        return appUserRepository.save(user);
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private String normalizePhone(String phone) {
        if (phone == null) {
            return null;
        }
        return phone.replaceAll("[^0-9]", "");
    }
}