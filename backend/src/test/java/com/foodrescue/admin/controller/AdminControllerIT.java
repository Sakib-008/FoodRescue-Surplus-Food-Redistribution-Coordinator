package com.foodrescue.admin.controller;

import com.foodrescue.support.AbstractIntegrationTest;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Controller tests of the admin API: role-based access, user CRUD and report export. */
class AdminControllerIT extends AbstractIntegrationTest {

    @Test
    void onlyAdminsCanManageUsers() throws Exception {
        Session donor = register("DONOR");
        mvc.perform(auth(get("/api/admin/users"), donor.token())).andExpect(status().isForbidden());

        String admin = loginAdmin();
        String email = "ops-" + UUID.randomUUID() + "@test.local";
        String created = mvc.perform(jsonBody(auth(post("/api/admin/users"), admin),
                        "{\"email\":\"%s\",\"password\":\"Secret-123\",\"fullName\":\"Ops\",\"role\":\"VOLUNTEER\"}".formatted(email)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = mapper.readTree(created).at("/data/id").asLong();

        mvc.perform(jsonBody(auth(put("/api/admin/users/" + id), admin), "{\"fullName\":\"Operations\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.fullName").value("Operations"));
        mvc.perform(auth(delete("/api/admin/users/" + id), admin)).andExpect(status().isNoContent());
        mvc.perform(auth(get("/api/admin/users/" + id), admin)).andExpect(status().isNotFound());
        mvc.perform(auth(get("/api/admin/stats"), admin)).andExpect(status().isOk());
    }

    @Test
    void adminCanDownloadReportsAsCsvOrText() throws Exception {
        String admin = loginAdmin();
        mvc.perform(auth(get("/api/admin/reports/rescued-food/export?format=csv"), admin))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"foodrescue-rescued-food.csv\""))
                .andExpect(content().string(startsWith("Category,Unit,Quantity")));
        mvc.perform(auth(get("/api/admin/reports/statistics/export?format=txt"), admin))
                .andExpect(status().isOk())
                .andExpect(content().string(startsWith("FoodRescue - dashboard statistics")));
        mvc.perform(auth(get("/api/admin/reports/statistics/export?format=pdf"), admin))
                .andExpect(status().isBadRequest());

        Session donor = register("DONOR");
        mvc.perform(auth(get("/api/admin/reports/rescued-food/export"), donor.token())).andExpect(status().isForbidden());
    }
}
