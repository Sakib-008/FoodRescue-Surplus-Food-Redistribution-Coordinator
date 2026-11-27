package com.foodrescue.discovery.util;

/**
 * Singleton pattern (as in the lab): a private constructor so no one can call {@code new}, a private static
 * instance, and a static {@code getInstance()} that creates it the first time and returns the same object after that.
 *
 * <p>Why here: distance is needed by the search, the recommender and the distance strategy. The calculator keeps
 * no per-request state, so one shared instance is enough. {@code synchronized} makes the lazy creation safe when
 * several web requests arrive at once.
 */
public class DistanceCalculator {

    private static final double EARTH_RADIUS_KM = 6371.0088;

    private static DistanceCalculator instance = null;

    private DistanceCalculator() { }

    public static synchronized DistanceCalculator getInstance() {
        if (instance == null) {
            instance = new DistanceCalculator();
        }
        return instance;
    }

    /** Great-circle distance in kilometres (Haversine formula). */
    public double km(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
