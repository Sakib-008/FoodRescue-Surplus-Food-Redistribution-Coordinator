package com.foodrescue.discovery.strategy;

import com.foodrescue.discovery.util.DistanceCalculator;
import com.foodrescue.donation.entity.Donation;
import java.time.Instant;

public class DistanceMatchStrategy implements MatchingStrategy {
    @Override public String name() { return "distance"; }
    @Override public double weight() { return 0.4; }

    @Override
    public double score(Donation d, MatchContext ctx, Instant now) {
        if (!ctx.hasLocation() || d.getLat() == null || d.getLng() == null) {
            return 0.5;   // unknown distance: neutral
        }
        double km = DistanceCalculator.getInstance().km(ctx.lat(), ctx.lng(), d.getLat(), d.getLng());
        return Math.max(0.0, 1.0 - km / ctx.radiusKm());
    }
}
