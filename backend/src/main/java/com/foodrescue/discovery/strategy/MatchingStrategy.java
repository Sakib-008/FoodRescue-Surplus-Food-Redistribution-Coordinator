package com.foodrescue.discovery.strategy;

import com.foodrescue.donation.entity.Donation;
import java.time.Instant;

/** Strategy pattern: each implementation scores one aspect of a donation/recipient match in [0, 1]. */
public interface MatchingStrategy {
    String name();

    /** Relative importance when strategies are combined. */
    double weight();

    double score(Donation donation, MatchContext context, Instant now);
}
