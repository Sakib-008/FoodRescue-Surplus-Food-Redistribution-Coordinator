package com.foodrescue.donation.entity;

import com.foodrescue.common.exception.ApiException;
import java.util.EnumSet;
import java.util.Set;

/** State pattern: the enum owns the transition table; entities and services only ask it. */
public enum DonationStatus {
    AVAILABLE, RESERVED, COMPLETED, EXPIRED, CANCELLED;

    public Set<DonationStatus> allowedNext() {
        return switch (this) {
            case AVAILABLE -> EnumSet.of(RESERVED, EXPIRED, CANCELLED);
            case RESERVED -> EnumSet.of(AVAILABLE, COMPLETED, EXPIRED, CANCELLED);
            case COMPLETED, EXPIRED, CANCELLED -> EnumSet.noneOf(DonationStatus.class);
        };
    }

    public boolean canTransitionTo(DonationStatus next) {
        return allowedNext().contains(next);
    }

    /** @throws ApiException 409 INVALID_STATUS_TRANSITION */
    public DonationStatus requireTransitionTo(DonationStatus next) {
        if (!canTransitionTo(next)) {
            throw ApiException.conflict("INVALID_STATUS_TRANSITION",
                    "Donation cannot move from " + this + " to " + next);
        }
        return next;
    }

    public boolean isActive() {
        return this == AVAILABLE || this == RESERVED;
    }
}
