package com.foodrescue.user.repository;

import com.foodrescue.user.entity.Role;
import com.foodrescue.user.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    Page<User> findByRole(Role role, Pageable pageable);

    /** Rows in other modules' tables that reference this user (shared schema, so plain SQL is used). */
    @Query(value = """
            SELECT (SELECT COUNT(*) FROM donations WHERE donor_id = :id)
                 + (SELECT COUNT(*) FROM donation_requests WHERE recipient_id = :id)
                 + (SELECT COUNT(*) FROM pickup_tasks WHERE volunteer_id = :id)
                 + (SELECT COUNT(*) FROM notifications WHERE user_id = :id)
            """, nativeQuery = true)
    long countActivity(@Param("id") Long id);
}
