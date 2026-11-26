package com.foodrescue.admin.service;

import com.foodrescue.admin.dto.AdminDtos.CreateUserRequest;
import com.foodrescue.admin.dto.AdminDtos.UpdateUserRequest;
import com.foodrescue.common.exception.ApiException;
import com.foodrescue.common.response.PageResponse;
import com.foodrescue.user.dto.UserDto;
import com.foodrescue.user.entity.Role;
import com.foodrescue.user.entity.User;
import com.foodrescue.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** User administration: CRUD plus enable/disable and role changes. */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public PageResponse<UserDto> listUsers(Role role, int page, int size) {
        Pageable pageable = PageRequest.of(page, Math.min(Math.max(size, 1), 100), Sort.by("id"));
        var result = role == null ? users.findAll(pageable) : users.findByRole(role, pageable);
        return PageResponse.from(result.map(UserDto::from));
    }

    @Transactional(readOnly = true)
    public UserDto getUser(Long id) {
        return UserDto.from(find(id));
    }

    @Transactional
    public UserDto createUser(CreateUserRequest req) {
        String email = req.email().trim().toLowerCase();
        if (users.existsByEmailIgnoreCase(email)) {
            throw ApiException.conflict("EMAIL_TAKEN", "Email is already registered");
        }
        User user = users.save(User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(req.password()))
                .fullName(req.fullName().trim())
                .phone(req.phone())
                .role(req.role())
                .enabled(true)
                .build());
        return UserDto.from(user);
    }

    @Transactional
    public UserDto updateUser(Long id, UpdateUserRequest req) {
        User user = find(id);
        user.setFullName(req.fullName().trim());
        user.setPhone(req.phone());
        user.setAddress(req.address());
        return UserDto.from(users.save(user));
    }

    /** Accounts that already took part in donations, requests or pickups must be disabled instead of deleted. */
    @Transactional
    public void deleteUser(Long adminId, Long id) {
        requireNotSelf(adminId, id);
        find(id);
        if (users.countActivity(id) > 0) {
            throw ApiException.conflict("USER_HAS_ACTIVITY",
                    "This user has donations, requests, pickups or notifications - disable the account instead");
        }
        users.deleteById(id);
    }

    @Transactional
    public UserDto setEnabled(Long adminId, Long userId, boolean enabled) {
        requireNotSelf(adminId, userId);
        User user = find(userId);
        user.setEnabled(enabled);
        return UserDto.from(users.save(user));
    }

    @Transactional
    public UserDto setRole(Long adminId, Long userId, Role role) {
        requireNotSelf(adminId, userId);
        User user = find(userId);
        user.setRole(role);
        return UserDto.from(users.save(user));
    }

    private void requireNotSelf(Long adminId, Long userId) {
        if (adminId.equals(userId)) {
            throw ApiException.badRequest("SELF_MODIFICATION", "You cannot change or delete your own account here");
        }
    }

    private User find(Long id) {
        return users.findById(id).orElseThrow(() -> ApiException.notFound("User"));
    }
}
