package com.foodrescue.contract;

import com.foodrescue.donation.entity.DonationStatus;
import java.math.BigDecimal;
import java.time.Instant;

/** Read model of a donation exposed by Member 2 to Member 3. */
public record DonationView(Long id, Long donorId, String foodName, String unit, DonationStatus status,
                           BigDecimal quantityTotal, BigDecimal quantityAvailable, Instant expiryAt,
                           String pickupAddress, Instant pickupFrom, Instant pickupTo) { }
