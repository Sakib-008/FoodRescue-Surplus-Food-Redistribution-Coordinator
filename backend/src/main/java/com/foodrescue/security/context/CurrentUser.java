package com.foodrescue.security.context;

import com.foodrescue.common.exception.ApiException;
import com.foodrescue.user.entity.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/** The only way other modules should ask "who is calling?". */
public final class CurrentUser {
    private CurrentUser() { }

    public static AuthUser get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthUser user)) {
            throw ApiException.unauthorized("UNAUTHORIZED", "Authentication required");
        }
        return user;
    }

    public static Long id() { return get().id(); }

    public static Role role() { return get().role(); }
}
