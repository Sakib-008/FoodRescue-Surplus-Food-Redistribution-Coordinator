package com.foodrescue.discovery.service;

import com.foodrescue.discovery.strategy.MatchContext;
import com.foodrescue.discovery.strategy.MatchingStrategy;
import com.foodrescue.discovery.strategy.MatchingStrategyFactory;
import com.foodrescue.discovery.util.DistanceCalculator;
import com.foodrescue.donation.dto.DonationDtos.DonationResponse;
import com.foodrescue.donation.entity.Donation;
import com.foodrescue.donation.entity.DonationStatus;
import com.foodrescue.donation.repository.DonationRepository;
import com.foodrescue.donation.service.DonationService;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Matches donations to a recipient by combining several MatchingStrategy objects (Strategy pattern, weighted
 * average). The strategies are created by MatchingStrategyFactory (Factory pattern).
 */
@Service
@RequiredArgsConstructor
public class RecommendationService {

    static final Duration MIN_PICKUP_LEAD = Duration.ofMinutes(15);

    private final DonationRepository donations;
    private final DonationService donationService;
    private final Clock clock;

    static final List<String> CRITERIA = List.of("DISTANCE", "CATEGORY", "EXPIRY");
    private final List<MatchingStrategy> strategies = createStrategies();

    private static List<MatchingStrategy> createStrategies() {
        MatchingStrategyFactory factory = new MatchingStrategyFactory();
        return CRITERIA.stream().map(factory::getStrategy).toList();
    }

    @Transactional(readOnly = true)
    public List<DonationResponse> recommend(MatchContext ctx, int limit) {
        Instant now = clock.instant();
        // pickup feasibility: the pickup window must still be open for at least MIN_PICKUP_LEAD
        List<Donation> candidates = donations.findRecommendationCandidates(
                DonationStatus.AVAILABLE, now, now.plus(MIN_PICKUP_LEAD));

        List<Donation> feasible = new ArrayList<>();
        Map<Long, Double> distances = new java.util.HashMap<>();
        for (Donation d : candidates) {
            if (ctx.hasLocation() && d.getLat() != null && d.getLng() != null) {
                double km = DistanceCalculator.getInstance().km(ctx.lat(), ctx.lng(), d.getLat(), d.getLng());
                if (km > ctx.radiusKm()) {
                    continue;
                }
                distances.put(d.getId(), Math.round(km * 10.0) / 10.0);
            }
            feasible.add(d);
        }

        Map<Long, Double> scores = feasible.stream()
                .collect(Collectors.toMap(Donation::getId, d -> combinedScore(d, ctx, now)));
        List<Donation> ranked = feasible.stream()
                .sorted(Comparator.comparing((Donation d) -> scores.get(d.getId())).reversed())
                .limit(Math.max(1, limit))
                .toList();

        return donationService.toResponses(ranked).stream()
                .map(r -> r.withDistanceKm(distances.get(r.id()))
                        .withMatchScore(Math.round(scores.get(r.id()) * 100.0) / 100.0))
                .sorted(Comparator.comparing(DonationResponse::matchScore).reversed())
                .toList();
    }

    double combinedScore(Donation d, MatchContext ctx, Instant now) {
        double total = 0;
        double weights = 0;
        for (MatchingStrategy s : strategies) {
            total += s.weight() * s.score(d, ctx, now);
            weights += s.weight();
        }
        return weights == 0 ? 0 : total / weights;
    }

    public static Set<Long> parseCategoryIds(List<Long> ids) {
        return ids == null ? Set.of() : Set.copyOf(ids);
    }
}
