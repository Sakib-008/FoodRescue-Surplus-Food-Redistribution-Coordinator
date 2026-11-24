package com.foodrescue.user.service;

import com.foodrescue.common.exception.ApiException;
import com.foodrescue.security.jwt.JwtService;
import com.foodrescue.user.dto.AuthDtos.LoginRequest;
import com.foodrescue.user.dto.AuthDtos.RegisterRequest;
import com.foodrescue.user.entity.Role;
import com.foodrescue.user.entity.User;
import com.foodrescue.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository users;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JwtService jwt = new JwtService("unit-test-secret-unit-test-secret-1234", 60);
    private AuthService service;

    @BeforeEach
    void setUp() {
        service = new AuthService(users, encoder, jwt);
    }

    private RegisterRequest register(String email, Role role) {
        return new RegisterRequest(email, "Password123", "Test User", null, role, null, null, null);
    }

    private User stored() {
        User u = User.builder().email("a@b.com").passwordHash(encoder.encode("Password123"))
                .fullName("T").role(Role.DONOR).build();
        u.setId(5L);
        return u;
    }

    @Test
    void registerStoresABCryptHashNotThePassword() {
        when(users.existsByEmailIgnoreCase("a@b.com")).thenReturn(false);
        when(users.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        service.register(register(" A@B.com ", Role.DONOR));

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(users).save(saved.capture());
        assertEquals("a@b.com", saved.getValue().getEmail());
        assertNotEquals("Password123", saved.getValue().getPasswordHash());
        assertTrue(encoder.matches("Password123", saved.getValue().getPasswordHash()));
    }

    @Test
    void adminAccountsCannotBeSelfRegistered() {
        ApiException ex = assertThrows(ApiException.class, () -> service.register(register("a@b.com", Role.ADMIN)));
        assertEquals(403, ex.getStatus());
        verify(users, never()).save(any());
    }

    @Test
    void duplicateEmailIsAConflict() {
        when(users.existsByEmailIgnoreCase("a@b.com")).thenReturn(true);
        ApiException ex = assertThrows(ApiException.class, () -> service.register(register("a@b.com", Role.RECIPIENT)));
        assertEquals("EMAIL_TAKEN", ex.getCode());
    }

    @Test
    void wrongPasswordIsRejected() {
        when(users.findByEmailIgnoreCase("a@b.com")).thenReturn(Optional.of(stored()));
        ApiException ex = assertThrows(ApiException.class, () -> service.login(new LoginRequest("a@b.com", "wrong")));
        assertEquals(401, ex.getStatus());
        assertEquals("INVALID_CREDENTIALS", ex.getCode());
    }

    @Test
    void logoutIncrementsTheTokenVersion() {
        User u = stored();
        when(users.findById(5L)).thenReturn(Optional.of(u));
        service.logout(5L);
        assertEquals(1, u.getTokenVersion());
    }
}
