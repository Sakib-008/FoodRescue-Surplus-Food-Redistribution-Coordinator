package com.foodrescue.donation.controller;

import com.foodrescue.support.AbstractIntegrationTest;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Controller tests of category CRUD (admin only). */
class CategoryControllerIT extends AbstractIntegrationTest {

    @Test
    void adminManagesCategoriesAndDonorsCannot() throws Exception {
        Session donor = register("DONOR");
        mvc.perform(jsonBody(auth(post("/api/categories"), donor.token()), "{\"name\":\"Mine\"}")).andExpect(status().isForbidden());

        String admin = loginAdmin();
        String name = "Soups " + UUID.randomUUID().toString().substring(0, 8);
        String created = mvc.perform(jsonBody(auth(post("/api/categories"), admin), "{\"name\":\"%s\"}".formatted(name)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = mapper.readTree(created).at("/data/id").asLong();

        mvc.perform(jsonBody(auth(put("/api/categories/" + id), admin), "{\"name\":\"%s hot\"}".formatted(name)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.name").value(name + " hot"));
        mvc.perform(auth(delete("/api/categories/" + id), admin)).andExpect(status().isNoContent());
    }
}
