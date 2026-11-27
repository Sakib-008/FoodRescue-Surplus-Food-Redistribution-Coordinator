package com.foodrescue.donation.service;

import com.foodrescue.common.exception.ApiException;
import com.foodrescue.donation.dto.DonationDtos.DonationForm;
import com.foodrescue.donation.dto.DonationDtos.DonationResponse;
import com.foodrescue.donation.entity.Allergen;
import com.foodrescue.donation.entity.Donation;
import com.foodrescue.donation.entity.DonationStatus;
import com.foodrescue.donation.entity.FoodCategory;
import com.foodrescue.donation.repository.AllergenRepository;
import com.foodrescue.donation.repository.DonationRepository;
import com.foodrescue.donation.repository.FoodCategoryRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DonationServiceTest {

    private static final Instant NOW = Instant.parse("2026-11-01T10:00:00Z");

    @Mock DonationRepository donations;
    @Mock FoodCategoryRepository categories;
    @Mock AllergenRepository allergens;
    private DonationService service;

    @BeforeEach
    void setUp() {
        service = new DonationService(donations, categories, allergens, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    private DonationForm form(Instant expiry, Instant pickupTo, List<Long> allergenIds) {
        return new DonationForm("Vegetable curry", 1L, "Fresh", new BigDecimal("10"), "kg", expiry, "12 Main Road",
                22.8, 89.5, NOW.plus(1, ChronoUnit.HOURS), pickupTo, "Keep chilled", allergenIds, null);
    }

    private DonationForm validForm(List<Long> allergenIds) {
        return form(NOW.plus(6, ChronoUnit.HOURS), NOW.plus(4, ChronoUnit.HOURS), allergenIds);
    }

    private void categoryExists() {
        FoodCategory c = new FoodCategory("Cooked Meals");
        c.setId(1L);
        when(categories.findById(1L)).thenReturn(Optional.of(c));
    }

    @Test
    void newDonationIsAvailableWithItsAllergens() {
        categoryExists();
        Allergen milk = new Allergen("Milk");
        milk.setId(7L);
        when(allergens.findAllById(Set.of(7L))).thenReturn(List.of(milk));
        when(donations.save(any(Donation.class))).thenAnswer(inv -> inv.getArgument(0));

        DonationResponse r = service.create(42L, validForm(List.of(7L)));

        assertEquals(DonationStatus.AVAILABLE, r.status());
        assertEquals(0, new BigDecimal("10").compareTo(r.quantityAvailable()));
        assertEquals("Milk", r.allergens().get(0).name());
    }

    @Test
    void pickupWindowMustEndBeforeTheFoodExpires() {
        ApiException ex = assertThrows(ApiException.class, () -> service.create(42L,
                form(NOW.plus(3, ChronoUnit.HOURS), NOW.plus(5, ChronoUnit.HOURS), List.of())));
        assertEquals("INVALID_PICKUP_WINDOW", ex.getCode());
    }

    @Test
    void unknownAllergenIdsAreRejected() {
        categoryExists();
        when(allergens.findAllById(Set.of(999L))).thenReturn(List.of());
        ApiException ex = assertThrows(ApiException.class, () -> service.create(42L, validForm(List.of(999L))));
        assertEquals("INVALID_ALLERGEN", ex.getCode());
        verify(donations, never()).save(any());
    }

    @Test
    void donationWithAcceptedRequestsCannotBeCancelled() {
        Donation d = Donation.builder().donorId(1L).category(new FoodCategory("X")).foodName("Curry")
                .quantityTotal(new BigDecimal("10")).quantityAvailable(new BigDecimal("6")).unit("kg")
                .expiryAt(NOW.plus(6, ChronoUnit.HOURS)).pickupAddress("x").pickupFrom(NOW).pickupTo(NOW.plus(2, ChronoUnit.HOURS))
                .status(DonationStatus.AVAILABLE).build();
        when(donations.findByIdForUpdate(9L)).thenReturn(Optional.of(d));

        ApiException ex = assertThrows(ApiException.class, () -> service.cancel(1L, 9L));

        assertEquals("DONATION_HAS_ALLOCATIONS", ex.getCode());
        assertEquals(DonationStatus.AVAILABLE, d.getStatus());
    }
}
