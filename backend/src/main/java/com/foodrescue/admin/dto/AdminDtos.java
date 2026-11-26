package com.foodrescue.admin.dto;

import com.foodrescue.user.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class AdminDtos {
    private AdminDtos() { }

    public record StatusRequest(@NotNull Boolean enabled) { }

    public record RoleRequest(@NotNull Role role) { }

    /** Admin-created account; unlike self-registration this may create another ADMIN. */
    public record CreateUserRequest(
            @NotBlank @Email @Size(max = 255) String email,
            @NotBlank @Size(min = 8, max = 72, message = "must be 8-72 characters") String password,
            @NotBlank @Size(max = 150) String fullName,
            @Size(max = 30) String phone,
            @NotNull Role role) { }

    public record UpdateUserRequest(
            @NotBlank @Size(max = 150) String fullName,
            @Size(max = 30) String phone,
            @Size(max = 500) String address) { }
}
