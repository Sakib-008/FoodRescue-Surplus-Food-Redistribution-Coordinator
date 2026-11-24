package com.foodrescue.security.jwt;

import com.foodrescue.user.entity.Role;
import com.foodrescue.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {

    private static final String SECRET = "unit-test-secret-unit-test-secret-1234";

    private User user() {
        User u = User.builder().email("a@b.com").role(Role.DONOR).build();
        u.setId(42L);
        return u;
    }

    @Test
    void tokenCarriesUserIdRoleAndTokenVersion() {
        JwtService jwt = new JwtService(SECRET, 60);
        Claims claims = jwt.parse(jwt.generate(user()));
        assertEquals("42", claims.getSubject());
        assertEquals("DONOR", claims.get("role", String.class));
        assertEquals(0, JwtService.versionOf(claims));
    }

    @Test
    void tamperedExpiredOrForeignTokensAreRejected() {
        JwtService jwt = new JwtService(SECRET, 60);
        String[] parts = jwt.generate(user()).split("\\.");
        parts[1] = parts[1].substring(0, 5) + (parts[1].charAt(5) == 'a' ? 'b' : 'a') + parts[1].substring(6);
        assertThrows(JwtException.class, () -> jwt.parse(String.join(".", parts)));

        JwtService expired = new JwtService(SECRET, -1);
        assertThrows(JwtException.class, () -> expired.parse(expired.generate(user())));

        String foreign = new JwtService("another-secret-another-secret-another-1", 60).generate(user());
        assertThrows(JwtException.class, () -> jwt.parse(foreign));
    }
}
