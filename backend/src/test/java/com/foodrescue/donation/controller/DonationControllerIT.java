package com.foodrescue.donation.controller;

import com.foodrescue.expiry.service.ExpiryService;
import com.foodrescue.support.AbstractIntegrationTest;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.foodrescue.support.DonationFixtures.anyCategoryId;
import static com.foodrescue.support.DonationFixtures.createDonation;
import static com.foodrescue.support.DonationFixtures.donationJson;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Controller tests of /api/donations. */
class DonationControllerIT extends AbstractIntegrationTest {

    @Autowired ExpiryService expiryService;

    @Test
    void donorCreatesADonationWithAllergensAndCanCancelIt() throws Exception {
        Session donor = register("DONOR");
        long category = anyCategoryId(mvc, mapper, donor.token());
        long allergen = mapper.readTree(mvc.perform(auth(get("/api/allergens"), donor.token())).andReturn()
                .getResponse().getContentAsString()).at("/data/0/id").asLong();
        String body = donationJson(category, "Custard tarts", "12").replace("\"allergenIds\":[]", "\"allergenIds\":[" + allergen + "]");

        String created = mvc.perform(jsonBody(auth(post("/api/donations"), donor.token()), body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("AVAILABLE"))
                .andExpect(jsonPath("$.data.allergens.length()").value(1))
                .andReturn().getResponse().getContentAsString();
        long id = mapper.readTree(created).at("/data/id").asLong();

        mvc.perform(auth(delete("/api/donations/" + id), donor.token())).andExpect(status().isOk());
        mvc.perform(auth(get("/api/donations/" + id), donor.token())).andExpect(jsonPath("$.data.status").value("CANCELLED"));
    }

    @Test
    void invalidDataIs400AndOnlyDonorsMayPost() throws Exception {
        Session donor = register("DONOR");
        Session recipient = register("RECIPIENT");
        long category = anyCategoryId(mvc, mapper, donor.token());

        mvc.perform(jsonBody(auth(post("/api/donations"), donor.token()), donationJson(category, "", "-5")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.fields.quantity").exists());
        mvc.perform(jsonBody(auth(post("/api/donations"), recipient.token()), donationJson(category, "Soup", "2")))
                .andExpect(status().isForbidden());
    }

    @Test
    void expiredFoodIsHiddenAndTheJobMarksItExpired() throws Exception {
        Session donor = register("DONOR");
        long id = createDonation(mvc, mapper, donor.token(), "10");
        jdbc.update("update donations set expiry_at = ?, pickup_from = ?, pickup_to = ? where id = ?",
                OffsetDateTime.now().minusHours(2), OffsetDateTime.now().minusHours(5), OffsetDateTime.now().minusHours(3), id);

        String browse = mvc.perform(auth(get("/api/donations").param("size", "50"), donor.token()))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertFalse(browse.contains("\"id\":" + id + ","));

        expiryService.runCycle();
        mvc.perform(auth(get("/api/donations/" + id), donor.token())).andExpect(jsonPath("$.data.status").value("EXPIRED"));
    }
}
