package com.foodrescue.user.service;

import com.foodrescue.common.exception.ApiException;
import com.foodrescue.security.jwt.JwtService;
import com.foodrescue.user.dto.AuthDtos.AuthResponse;
import com.foodrescue.user.dto.AuthDtos.LoginRequest;
import com.foodrescue.user.dto.AuthDtos.RegisterRequest;
import com.foodrescue.user.dto.UserDto;
import com.foodrescue.user.entity.Role;
import com.foodrescue.user.entity.User;
import com.foodrescue.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (req.role() == Role.ADMIN) {
            throw ApiException.forbidden("Admin accounts cannot be self-registered");
        }
        String email = req.email().trim().toLowerCase();
        if (users.existsByEmailIgnoreCase(email)) {
            throw ApiException.conflict("EMAIL_TAKEN", "Email is already registered");
        }
        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(req.password()))
                .fullName(req.fullName().trim())
                .phone(req.phone())
                .role(req.role())
                .address(req.address())
                .lat(req.lat())
                .lng(req.lng())
                .enabled(true)
                .build();
        users.save(user);
        return new AuthResponse(jwtService.generate(user), UserDto.from(user));
    }

    /** Logout = increment the token version, which revokes every token issued to this user so far. */
    @Transactional
    public void logout(Long userId) {
        User user = users.findById(userId).orElseThrow(() -> ApiException.notFound("User"));
        user.invalidateTokens();
        users.save(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        User user = users.findByEmailIgnoreCase(req.email().trim())
                .orElseThrow(() -> ApiException.unauthorized("INVALID_CREDENTIALS", "Invalid email or password"));
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw ApiException.unauthorized("INVALID_CREDENTIALS", "Invalid email or password");
        }
        if (!user.isEnabled()) {
            throw new ApiException(403, "ACCOUNT_DISABLED", "This account has been disabled");
        }
        return new AuthResponse(jwtService.generate(user), UserDto.from(user));
    }
}
