package com.foodrescue.discovery.strategy;

import com.foodrescue.donation.entity.Donation;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatchingStrategyTest {

    private static final Instant NOW = Instant.parse("2026-11-01T10:00:00Z");

    @Test
    void closerAndSoonerExpiringFoodScoresHigher() {
        MatchContext ctx = new MatchContext(Set.of(), 23.0, 90.0, 50);
        Donation near = Donation.builder().lat(23.0).lng(90.0).expiryAt(NOW.plus(1, ChronoUnit.HOURS)).build();
        Donation far = Donation.builder().lat(24.0).lng(90.0).expiryAt(NOW.plus(40, ChronoUnit.HOURS)).build();

        assertEquals(1.0, new DistanceMatchStrategy().score(near, ctx, NOW), 1e-9);
        assertEquals(0.0, new DistanceMatchStrategy().score(far, ctx, NOW), 1e-9);    // ~111 km, outside 50 km
        assertTrue(new ExpiryUrgencyStrategy().score(near, ctx, NOW) > new ExpiryUrgencyStrategy().score(far, ctx, NOW));
    }

    /** Factory pattern: the client names the product, the factory picks the concrete class. */
    @Test
    void factoryCreatesTheRequestedStrategy() {
        MatchingStrategyFactory factory = new MatchingStrategyFactory();

        assertInstanceOf(DistanceMatchStrategy.class, factory.getStrategy("DISTANCE"));
        assertInstanceOf(CategoryMatchStrategy.class, factory.getStrategy("category"));
        assertInstanceOf(ExpiryUrgencyStrategy.class, factory.getStrategy("EXPIRY"));
        assertNull(factory.getStrategy(null));
        assertThrows(IllegalArgumentException.class, () -> factory.getStrategy("PRICE"));
    }
}
