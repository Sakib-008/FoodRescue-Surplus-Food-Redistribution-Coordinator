package com.foodrescue.discovery.strategy;

import com.foodrescue.donation.entity.Donation;
import java.time.Instant;

public class CategoryMatchStrategy implements MatchingStrategy {
    @Override public String name() { return "category"; }
    @Override public double weight() { return 0.4; }

    @Override
    public double score(Donation d, MatchContext ctx, Instant now) {
        if (ctx.preferredCategoryIds() == null || ctx.preferredCategoryIds().isEmpty()) {
            return 0.5;   // no stated preference: neutral
        }
        return ctx.preferredCategoryIds().contains(d.getCategory().getId()) ? 1.0 : 0.0;
    }
}
