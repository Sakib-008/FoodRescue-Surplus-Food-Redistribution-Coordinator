package com.foodrescue.user.controller;

import com.foodrescue.common.response.ApiResponse;
import com.foodrescue.security.context.CurrentUser;
import com.foodrescue.user.dto.AuthDtos.AuthResponse;
import com.foodrescue.user.dto.AuthDtos.LoginRequest;
import com.foodrescue.user.dto.AuthDtos.RegisterRequest;
import com.foodrescue.user.dto.AuthDtos.UpdateProfileRequest;
import com.foodrescue.user.dto.UserDto;
import com.foodrescue.user.service.AuthService;
import com.foodrescue.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Registration, login, logout and the signed-in user's own profile. */
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/api/auth/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(authService.register(req)));
    }

    @PostMapping("/api/auth/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ApiResponse.ok(authService.login(req));
    }

    /** Revokes every token issued to the caller so far. */
    @PostMapping("/api/auth/logout")
    public ResponseEntity<Void> logout() {
        authService.logout(CurrentUser.id());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/users/me")
    public ApiResponse<UserDto> me() {
        return ApiResponse.ok(userService.getProfile(CurrentUser.id()));
    }

    @PutMapping("/api/users/me")
    public ApiResponse<UserDto> updateMe(@Valid @RequestBody UpdateProfileRequest req) {
        return ApiResponse.ok(userService.updateProfile(CurrentUser.id(), req));
    }
}
