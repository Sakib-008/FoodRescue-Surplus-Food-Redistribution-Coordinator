package com.foodrescue.user.controller;

import com.foodrescue.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Controller tests of registration, login, logout and authentication. */
class AuthControllerIT extends AbstractIntegrationTest {

    @Test
    void registeredUserIsStoredWithABCryptHashAndCanReadTheirProfile() throws Exception {
        Session s = register("DONOR");

        String hash = jdbc.queryForObject("select password_hash from users where email = ?", String.class, s.email());
        assertTrue(hash.startsWith("$2"));
        mvc.perform(auth(get("/api/users/me"), s.token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(s.email()))
                .andExpect(jsonPath("$.data.passwordHash").doesNotExist());
    }

    @Test
    void badInputWrongPasswordAndMissingTokenAreRejected() throws Exception {
        mvc.perform(jsonBody(post("/api/auth/register"), "{\"email\":\"nope\",\"password\":\"short\",\"fullName\":\"\",\"role\":\"DONOR\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.error.fields.email").exists());

        Session s = register("RECIPIENT");
        mvc.perform(jsonBody(post("/api/auth/login"), "{\"email\":\"%s\",\"password\":\"wrong-pass\"}".formatted(s.email())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("INVALID_CREDENTIALS"));

        mvc.perform(get("/api/users/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void logoutRevokesTheToken() throws Exception {
        Session s = register("VOLUNTEER");
        mvc.perform(auth(post("/api/auth/logout"), s.token())).andExpect(status().isNoContent());

        mvc.perform(auth(get("/api/users/me"), s.token())).andExpect(status().isUnauthorized());
        mvc.perform(auth(get("/api/users/me"), login(s.email(), PASSWORD))).andExpect(status().isOk());
    }
}
