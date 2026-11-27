package com.foodrescue.discovery.service;

import com.foodrescue.common.response.PageResponse;
import com.foodrescue.discovery.dto.DiscoveryFilter;
import com.foodrescue.discovery.util.DistanceCalculator;
import com.foodrescue.donation.dto.DonationDtos.DonationResponse;
import com.foodrescue.donation.entity.Donation;
import com.foodrescue.donation.entity.DonationStatus;
import com.foodrescue.donation.repository.DonationRepository;
import com.foodrescue.donation.service.DonationService;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DiscoveryService {

    private final DonationRepository donations;
    private final DonationService donationService;
    private final Clock clock;

    /**
     * Only AVAILABLE, unexpired donations with stock left are ever returned, so expired or exhausted food can
     * never be discovered (and the request service re-checks anyway). Distance filtering is done in memory
     * after the SQL filter, which is fine for the data volume of this project.
     */
    @Transactional(readOnly = true)
    public PageResponse<DonationResponse> search(DiscoveryFilter filter, int page, int size) {
        Instant now = clock.instant();
        Specification<Donation> spec = (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            p.add(cb.equal(root.get("status"), DonationStatus.AVAILABLE));
            p.add(cb.greaterThan(root.<Instant>get("expiryAt"), now));
            p.add(cb.greaterThan(root.<BigDecimal>get("quantityAvailable"), BigDecimal.ZERO));
            if (filter.categoryId() != null) {
                p.add(cb.equal(root.get("category").get("id"), filter.categoryId()));
            }
            if (filter.q() != null && !filter.q().isBlank()) {
                String like = "%" + filter.q().trim().toLowerCase() + "%";
                p.add(cb.or(cb.like(cb.lower(root.<String>get("foodName")), like),
                        cb.like(cb.lower(root.<String>get("description")), like)));
            }
            if (filter.minQuantity() != null) {
                p.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("quantityAvailable"), filter.minQuantity()));
            }
            if (filter.expiryBefore() != null) {
                p.add(cb.lessThanOrEqualTo(root.<Instant>get("expiryAt"), filter.expiryBefore()));
            }
            return cb.and(p.toArray(new Predicate[0]));
        };

        List<Donation> found = donations.findAll(spec, Sort.by(Sort.Direction.ASC, "expiryAt"));
        List<DonationResponse> results = new ArrayList<>();
        for (DonationResponse r : donationService.toResponses(found)) {
            if (filter.hasLocation()) {
                Double km = (r.lat() != null && r.lng() != null)
                        ? round(DistanceCalculator.getInstance().km(filter.lat(), filter.lng(), r.lat(), r.lng())) : null;
                if (filter.radiusKm() != null && (km == null || km > filter.radiusKm())) {
                    continue;   // outside the requested radius (or location unknown)
                }
                r = r.withDistanceKm(km);
            }
            results.add(r);
        }
        return PageResponse.of(results, page, size);
    }

    private static double round(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
