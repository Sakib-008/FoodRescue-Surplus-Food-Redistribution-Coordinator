package com.foodrescue.donation.controller;

import com.foodrescue.common.response.ApiResponse;
import com.foodrescue.common.response.PageResponse;
import com.foodrescue.discovery.dto.DiscoveryFilter;
import com.foodrescue.discovery.service.DiscoveryService;
import com.foodrescue.discovery.service.RecommendationService;
import com.foodrescue.discovery.strategy.MatchContext;
import com.foodrescue.donation.dto.DonationDtos.DonationForm;
import com.foodrescue.donation.dto.DonationDtos.DonationResponse;
import com.foodrescue.donation.service.DonationService;
import com.foodrescue.security.context.CurrentUser;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;
    private final DiscoveryService discoveryService;
    private final RecommendationService recommendationService;

    @PostMapping("/api/donations")
    @PreAuthorize("hasRole('DONOR')")
    public ResponseEntity<ApiResponse<DonationResponse>> create(@Valid @RequestBody DonationForm form) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(donationService.create(CurrentUser.id(), form)));
    }

    @PutMapping("/api/donations/{id}")
    @PreAuthorize("hasRole('DONOR')")
    public ApiResponse<DonationResponse> update(@PathVariable Long id, @Valid @RequestBody DonationForm form) {
        return ApiResponse.ok(donationService.update(CurrentUser.id(), id, form));
    }

    @DeleteMapping("/api/donations/{id}")
    @PreAuthorize("hasRole('DONOR')")
    public ApiResponse<Void> cancel(@PathVariable Long id) {
        donationService.cancel(CurrentUser.id(), id);
        return ApiResponse.ok(null, "Donation cancelled");
    }

    @GetMapping("/api/donations/mine")
    @PreAuthorize("hasRole('DONOR')")
    public ApiResponse<List<DonationResponse>> mine() {
        return ApiResponse.ok(donationService.mine(CurrentUser.id()));
    }


    @GetMapping("/api/donations/{id}")
    public ApiResponse<DonationResponse> get(@PathVariable Long id) {
        return ApiResponse.ok(donationService.get(id));
    }

    /** Browse: only AVAILABLE, unexpired donations with stock left. Optional search, filters and radius. */
    @GetMapping("/api/donations")
    public ApiResponse<PageResponse<DonationResponse>> browse(
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) BigDecimal minQty,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant expiryBefore,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) Double radiusKm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        DiscoveryFilter filter = new DiscoveryFilter(category, q, minQty, expiryBefore, lat, lng, radiusKm);
        return ApiResponse.ok(discoveryService.search(filter, page, Math.min(size, 50)));
    }

    /** Ranked by the matching strategies (category, distance, expiry urgency). */
    @GetMapping("/api/donations/recommended")
    @PreAuthorize("hasRole('RECIPIENT')")
    public ApiResponse<List<DonationResponse>> recommended(
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(defaultValue = "25") double radiusKm,
            @RequestParam(defaultValue = "10") int limit) {
        MatchContext ctx = new MatchContext(RecommendationService.parseCategoryIds(categories), lat, lng, Math.max(radiusKm, 0.1));
        return ApiResponse.ok(recommendationService.recommend(ctx, Math.min(limit, 50)));
    }
}
