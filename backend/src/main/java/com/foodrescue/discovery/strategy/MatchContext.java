package com.foodrescue.discovery.strategy;

import java.util.Set;

public record MatchContext(Set<Long> preferredCategoryIds, Double lat, Double lng, double radiusKm) {
    public boolean hasLocation() {
        return lat != null && lng != null;
    }
}
