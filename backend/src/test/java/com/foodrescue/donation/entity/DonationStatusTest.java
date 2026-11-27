package com.foodrescue.donation.entity;

import com.foodrescue.common.exception.ApiException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DonationStatusTest {

    @Test
    void onlyValidTransitionsAreAllowed() {
        assertTrue(DonationStatus.AVAILABLE.canTransitionTo(DonationStatus.RESERVED));
        assertTrue(DonationStatus.AVAILABLE.canTransitionTo(DonationStatus.EXPIRED));
        assertTrue(DonationStatus.RESERVED.canTransitionTo(DonationStatus.COMPLETED));
        assertFalse(DonationStatus.AVAILABLE.canTransitionTo(DonationStatus.COMPLETED));
        assertTrue(DonationStatus.EXPIRED.allowedNext().isEmpty());
        ApiException ex = assertThrows(ApiException.class, () -> DonationStatus.EXPIRED.requireTransitionTo(DonationStatus.AVAILABLE));
        assertEquals(409, ex.getStatus());
    }
}
