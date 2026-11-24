package com.foodrescue.user.config;

import com.foodrescue.user.entity.Role;
import com.foodrescue.user.entity.User;
import com.foodrescue.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Creates the first ADMIN from ADMIN_EMAIL / ADMIN_PASSWORD when no such user exists. */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminBootstrap implements CommandLineRunner {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    @Value("${app.admin.email}")
    private String email;
    @Value("${app.admin.password:}")
    private String password;
    @Value("${app.admin.full-name:System Admin}")
    private String fullName;

    @Override
    public void run(String... args) {
        if (password == null || password.isBlank()) {
            log.warn("ADMIN_PASSWORD not set - no bootstrap admin created");
            return;
        }
        if (users.existsByEmailIgnoreCase(email)) {
            return;
        }
        users.save(User.builder().email(email.toLowerCase()).passwordHash(encoder.encode(password))
                .fullName(fullName).role(Role.ADMIN).enabled(true).build());
        log.info("Bootstrap admin created: {}", email);
    }
}
