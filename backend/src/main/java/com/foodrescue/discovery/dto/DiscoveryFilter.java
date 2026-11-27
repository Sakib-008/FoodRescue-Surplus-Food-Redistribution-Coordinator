package com.foodrescue.discovery.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record DiscoveryFilter(Long categoryId, String q, BigDecimal minQuantity, Instant expiryBefore,
                              Double lat, Double lng, Double radiusKm) {
    public boolean hasLocation() {
        return lat != null && lng != null;
    }
}
