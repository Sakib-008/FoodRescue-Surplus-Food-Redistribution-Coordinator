package com.foodrescue.contract;

import java.util.Collection;
import java.util.Map;

/** Provided by Member 1 (user module). Lets other modules show names without touching the User entity. */
public interface UserDirectory {
    Map<Long, String> displayNames(Collection<Long> userIds);

    default String displayName(Long userId) {
        return displayNames(java.util.List.of(userId)).getOrDefault(userId, "User #" + userId);
    }
}
