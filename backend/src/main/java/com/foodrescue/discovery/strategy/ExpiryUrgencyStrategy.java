package com.foodrescue.discovery.strategy;

import com.foodrescue.donation.entity.Donation;
import java.time.Duration;
import java.time.Instant;

/** Food that expires sooner is rescued first: score falls linearly from 1 (now) to 0 (48h or more away). */
public class ExpiryUrgencyStrategy implements MatchingStrategy {
    private static final double HORIZON_HOURS = 48.0;

    @Override public String name() { return "expiry-urgency"; }
    @Override public double weight() { return 0.2; }

    @Override
    public double score(Donation d, MatchContext ctx, Instant now) {
        double hoursLeft = Duration.between(now, d.getExpiryAt()).toMinutes() / 60.0;
        return 1.0 - Math.min(Math.max(hoursLeft, 0.0), HORIZON_HOURS) / HORIZON_HOURS;
    }
}
