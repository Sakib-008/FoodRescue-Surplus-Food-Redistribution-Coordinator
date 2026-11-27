package com.foodrescue.donation.service;

import com.foodrescue.common.exception.ApiException;
import com.foodrescue.contract.DonationInventoryService;
import com.foodrescue.contract.DonationView;
import com.foodrescue.donation.entity.Donation;
import com.foodrescue.donation.entity.DonationStatus;
import com.foodrescue.donation.repository.DonationRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements the contract Member 3 depends on. Every mutating method takes a row lock, so two recipients
 * being accepted at the same moment can never allocate more than quantity_available.
 */
@Service
@RequiredArgsConstructor
public class DonationInventoryServiceImpl implements DonationInventoryService {

    private final DonationRepository donations;
    private final Clock clock;

    @Override
    @Transactional(readOnly = true)
    public DonationView getForRequest(Long donationId) {
        return toView(donations.findById(donationId).orElseThrow(() -> ApiException.notFound("Donation")));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, DonationView> getViews(Collection<Long> donationIds) {
        Map<Long, DonationView> result = new HashMap<>();
        donations.findAllById(donationIds).forEach(d -> result.put(d.getId(), toView(d)));
        return result;
    }

    @Override
    @Transactional
    public void reserve(Long donationId, BigDecimal quantity) {
        if (quantity == null || quantity.signum() <= 0) {
            throw ApiException.badRequest("INVALID_QUANTITY", "Quantity must be greater than 0");
        }
        Donation donation = donations.findByIdForUpdate(donationId)
                .orElseThrow(() -> ApiException.notFound("Donation"));
        if (donation.getStatus() == DonationStatus.EXPIRED || donation.isExpiredAt(clock.instant())) {
            throw ApiException.conflict("DONATION_EXPIRED", "This donation has expired");
        }
        if (donation.getStatus() != DonationStatus.AVAILABLE) {
            throw ApiException.conflict("DONATION_UNAVAILABLE", "This donation is " + donation.getStatus());
        }
        if (quantity.compareTo(donation.getQuantityAvailable()) > 0) {
            throw ApiException.conflict("INSUFFICIENT_QUANTITY",
                    "Only " + donation.getQuantityAvailable() + " " + donation.getUnit() + " still available");
        }
        donation.setQuantityAvailable(donation.getQuantityAvailable().subtract(quantity));
        if (donation.getQuantityAvailable().signum() == 0) {
            donation.transitionTo(DonationStatus.RESERVED);
        }
        donations.save(donation);
    }

    @Override
    @Transactional
    public void release(Long donationId, BigDecimal quantity) {
        Donation donation = donations.findByIdForUpdate(donationId)
                .orElseThrow(() -> ApiException.notFound("Donation"));
        BigDecimal restored = donation.getQuantityAvailable().add(quantity);
        if (restored.compareTo(donation.getQuantityTotal()) > 0) {
            restored = donation.getQuantityTotal();
        }
        donation.setQuantityAvailable(restored);
        if (donation.getStatus() == DonationStatus.RESERVED && restored.signum() > 0) {
            donation.transitionTo(DonationStatus.AVAILABLE);
        }
        donations.save(donation);
    }

    @Override
    @Transactional
    public void markCompleted(Long donationId) {
        Donation donation = donations.findByIdForUpdate(donationId)
                .orElseThrow(() -> ApiException.notFound("Donation"));
        if (donation.getStatus() == DonationStatus.RESERVED) {
            donation.transitionTo(DonationStatus.COMPLETED);
            donations.save(donation);
        }
    }

    private DonationView toView(Donation d) {
        return new DonationView(d.getId(), d.getDonorId(), d.getFoodName(), d.getUnit(), d.getStatus(),
                d.getQuantityTotal(), d.getQuantityAvailable(), d.getExpiryAt(), d.getPickupAddress(),
                d.getPickupFrom(), d.getPickupTo());
    }
}
