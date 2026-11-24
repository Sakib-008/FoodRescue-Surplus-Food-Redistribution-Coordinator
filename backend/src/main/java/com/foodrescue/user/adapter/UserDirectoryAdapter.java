package com.foodrescue.user.adapter;

import com.foodrescue.contract.UserDirectory;
import com.foodrescue.user.entity.User;
import com.foodrescue.user.repository.UserRepository;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Adapter pattern (as in the lab's Car -> Railroad example).
 * <ul>
 *   <li>Target: {@link UserDirectory} - what Member 3's modules expect: "give me display names for these ids".</li>
 *   <li>Adaptee: {@link UserRepository} - what Member 1 has: {@code findAllById(...)} returning {@link User} entities.</li>
 *   <li>Adapter: this class implements the target and translates each call into a call on the adaptee.</li>
 * </ul>
 * Why here: the request/pickup code must not see the User entity or the repository (module independence), and
 * the two interfaces do not match, so a small adapter connects them.
 */
@Component
public class UserDirectoryAdapter implements UserDirectory {

    private final UserRepository userRepository;

    public UserDirectoryAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, String> displayNames(Collection<Long> userIds) {
        Map<Long, String> names = new HashMap<>();
        for (User user : userRepository.findAllById(userIds)) {
            names.put(user.getId(), user.getFullName());
        }
        return names;
    }
}
