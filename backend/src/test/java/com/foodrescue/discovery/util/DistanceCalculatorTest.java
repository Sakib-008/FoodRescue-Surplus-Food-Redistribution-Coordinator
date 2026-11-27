package com.foodrescue.discovery.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/** Singleton pattern: every caller gets the same instance, and it computes the right distance. */
class DistanceCalculatorTest {

    @Test
    void getInstanceAlwaysReturnsTheSameObject() {
        DistanceCalculator x = DistanceCalculator.getInstance();
        DistanceCalculator y = DistanceCalculator.getInstance();

        assertSame(x, y);
        assertEquals(111.2, x.km(23.0, 90.0, 24.0, 90.0), 0.1);   // one degree of latitude
    }
}
