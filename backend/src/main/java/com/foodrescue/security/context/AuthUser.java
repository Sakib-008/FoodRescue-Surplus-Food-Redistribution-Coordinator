package com.foodrescue.security.context;

import com.foodrescue.user.entity.Role;

/** Authenticated principal placed in the SecurityContext by JwtAuthFilter. */
public record AuthUser(Long id, String email, Role role) { }
