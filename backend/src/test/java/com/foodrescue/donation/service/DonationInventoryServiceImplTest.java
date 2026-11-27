package com.foodrescue.donation.service;

import com.foodrescue.common.exception.ApiException;
import com.foodrescue.donation.entity.Donation;
import com.foodrescue.donation.entity.DonationStatus;
import com.foodrescue.donation.entity.FoodCategory;
import com.foodrescue.donation.repository.DonationRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/** The stock rules behind "accepted requests can never exceed the available quantity". */
@ExtendWith(MockitoExtension.class)
class DonationInventoryServiceImplTest {

    private static final Instant NOW = Instant.parse("2026-11-01T10:00:00Z");

    @Mock DonationRepository donations;
    private DonationInventoryServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new DonationInventoryServiceImpl(donations, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    private Donation donation(String available, Instant expiry) {
        Donation d = Donation.builder().donorId(1L).category(new FoodCategory("X")).foodName("Rice")
                .quantityTotal(new BigDecimal("10")).quantityAvailable(new BigDecimal(available)).unit("kg")
                .expiryAt(expiry).pickupAddress("x").pickupFrom(NOW).pickupTo(expiry).status(DonationStatus.AVAILABLE).build();
        when(donations.findByIdForUpdate(5L)).thenReturn(Optional.of(d));
        return d;
    }

    @Test
    void reservingAllStockMarksTheDonationReserved() {
        Donation d = donation("10", NOW.plus(5, ChronoUnit.HOURS));
        service.reserve(5L, new BigDecimal("4"));
        assertEquals(0, new BigDecimal("6").compareTo(d.getQuantityAvailable()));
        service.reserve(5L, new BigDecimal("6"));
        assertEquals(DonationStatus.RESERVED, d.getStatus());
    }

    @Test
    void reservingMoreThanAvailableIsRejected() {
        Donation d = donation("3", NOW.plus(5, ChronoUnit.HOURS));
        ApiException ex = assertThrows(ApiException.class, () -> service.reserve(5L, new BigDecimal("3.01")));
        assertEquals("INSUFFICIENT_QUANTITY", ex.getCode());
        assertEquals(0, new BigDecimal("3").compareTo(d.getQuantityAvailable()));
    }

    @Test
    void expiredFoodCannotBeReservedEvenBeforeTheExpiryJobRan() {
        donation("10", NOW.minus(1, ChronoUnit.MINUTES));
        ApiException ex = assertThrows(ApiException.class, () -> service.reserve(5L, BigDecimal.ONE));
        assertEquals("DONATION_EXPIRED", ex.getCode());
    }
}
