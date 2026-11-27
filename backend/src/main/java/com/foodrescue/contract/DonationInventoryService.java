package com.foodrescue.contract;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

/**
 * Provided by Member 2 (donation module); consumed by Member 3 (request/pickup modules).
 * All mutating methods lock the donation row (SELECT ... FOR UPDATE) and must be called inside the caller's
 * transaction so quantity changes and request status changes commit or roll back together.
 */
public interface DonationInventoryService {

    /** @throws com.foodrescue.common.exception.ApiException 404 if unknown */
    DonationView getForRequest(Long donationId);

    Map<Long, DonationView> getViews(Collection<Long> donationIds);

    /** Allocates quantity. 409 DONATION_EXPIRED / DONATION_UNAVAILABLE / INSUFFICIENT_QUANTITY on violation. */
    void reserve(Long donationId, BigDecimal quantity);

    /** Gives allocated quantity back (e.g. accepted request cancelled). */
    void release(Long donationId, BigDecimal quantity);

    /** RESERVED -> COMPLETED once everything was delivered. No-op in any other state. */
    void markCompleted(Long donationId);
}
