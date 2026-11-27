package com.foodrescue.admin.service;

import com.foodrescue.admin.dto.AdminDtos.CreateUserRequest;
import com.foodrescue.common.exception.ApiException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock UserRepository users;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private AdminService service;

    @BeforeEach
    void setUp() {
        service = new AdminService(users, encoder);
    }

    @Test
    void adminCanCreateAnotherAdminWithAHashedPassword() {
        when(users.existsByEmailIgnoreCase("ops@test.local")).thenReturn(false);
        when(users.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        service.createUser(new CreateUserRequest("ops@test.local", "Secret-123", "Ops", null, Role.ADMIN));

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(users).save(saved.capture());
        assertEquals(Role.ADMIN, saved.getValue().getRole());
        assertTrue(encoder.matches("Secret-123", saved.getValue().getPasswordHash()));
    }

    @Test
    void userWithActivityCannotBeDeleted() {
        User u = User.builder().email("d@test.local").passwordHash("x").fullName("D").role(Role.DONOR).build();
        u.setId(5L);
        when(users.findById(5L)).thenReturn(Optional.of(u));
        when(users.countActivity(5L)).thenReturn(3L);

        ApiException ex = assertThrows(ApiException.class, () -> service.deleteUser(1L, 5L));

        assertEquals("USER_HAS_ACTIVITY", ex.getCode());
        verify(users, never()).deleteById(anyLong());
    }
}
