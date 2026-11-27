package com.foodrescue.discovery.strategy;

/**
 * Factory pattern (as in the lab's ShapeFactory): the client asks for a product by name and gets back the
 * interface type ({@link MatchingStrategy}); only the factory knows the concrete classes and calls {@code new}.
 *
 * <p>Why here: RecommendationService only lists which criteria it wants ("DISTANCE", "CATEGORY", "EXPIRY").
 * Adding a new criterion means one new class plus one branch here, and the service does not change.
 */
public class MatchingStrategyFactory {

    public MatchingStrategy getStrategy(String strategyType) {
        if (strategyType == null) {
            return null;
        }
        if (strategyType.equalsIgnoreCase("DISTANCE")) {
            return new DistanceMatchStrategy();
        } else if (strategyType.equalsIgnoreCase("CATEGORY")) {
            return new CategoryMatchStrategy();
        } else if (strategyType.equalsIgnoreCase("EXPIRY")) {
            return new ExpiryUrgencyStrategy();
        }
        throw new IllegalArgumentException("Unknown matching strategy: " + strategyType);
    }
}
