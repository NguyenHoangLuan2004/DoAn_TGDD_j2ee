package com.hutech.demo.service;

import com.hutech.demo.model.AppUser;
import com.hutech.demo.repository.AppUserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;

    public AuthService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public Optional<AppUser> login(String email, String password) {
        return appUserRepository.findByEmailIgnoreCase(email)
                .filter(user -> user.getPassword().equals(password));
    }

    public Optional<AppUser> getById(Long id) {
        return appUserRepository.findById(id);
    }
}
