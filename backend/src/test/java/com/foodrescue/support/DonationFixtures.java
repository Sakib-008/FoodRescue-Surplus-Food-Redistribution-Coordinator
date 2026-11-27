package com.foodrescue.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Creates donations through the real API (owner: Member 2; also used by Member 3's integration tests). */
public final class DonationFixtures {

    private DonationFixtures() { }

    public static long anyCategoryId(MockMvc mvc, ObjectMapper mapper, String token) throws Exception {
        MvcResult r = mvc.perform(get("/api/categories").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andReturn();
        return mapper.readTree(r.getResponse().getContentAsString()).at("/data/0/id").asLong();
    }

    /** JSON for a valid donation: expires in 8 h, pickup window from +1 h to +5 h. */
    public static String donationJson(long categoryId, String foodName, String quantity) {
        Instant now = Instant.now();
        return """
                {"foodName":"%s","categoryId":%d,"description":"Fresh today","quantity":%s,"unit":"portions",
                 "expiryAt":"%s","pickupAddress":"12 Main Road, Khulna","lat":22.8456,"lng":89.5403,
                 "pickupFrom":"%s","pickupTo":"%s","storageConditions":"Keep chilled",
                 "allergenIds":[],"allergenNotes":null}
                """.formatted(foodName, categoryId, quantity, now.plus(8, ChronoUnit.HOURS),
                now.plus(1, ChronoUnit.HOURS), now.plus(5, ChronoUnit.HOURS));
    }

    /** Creates a donation as the given donor and returns its id. */
    public static long createDonation(MockMvc mvc, ObjectMapper mapper, String donorToken, String quantity) throws Exception {
        long categoryId = anyCategoryId(mvc, mapper, donorToken);
        MvcResult r = mvc.perform(post("/api/donations").header("Authorization", "Bearer " + donorToken)
                        .contentType(MediaType.APPLICATION_JSON).content(donationJson(categoryId, "Vegetable curry", quantity)))
                .andExpect(status().isCreated()).andReturn();
        return mapper.readTree(r.getResponse().getContentAsString()).at("/data/id").asLong();
    }
}
