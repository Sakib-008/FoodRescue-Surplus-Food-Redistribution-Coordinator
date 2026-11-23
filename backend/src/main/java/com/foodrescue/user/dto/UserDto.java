package com.foodrescue.user.dto;

import com.foodrescue.user.entity.Role;
import com.foodrescue.user.entity.User;
import java.time.Instant;

public record UserDto(Long id, String email, String fullName, String phone, Role role, String address,
                      Double lat, Double lng, boolean enabled, Instant createdAt) {
    public static UserDto from(User u) {
        return new UserDto(u.getId(), u.getEmail(), u.getFullName(), u.getPhone(), u.getRole(), u.getAddress(),
                u.getLat(), u.getLng(), u.isEnabled(), u.getCreatedAt());
    }
}
