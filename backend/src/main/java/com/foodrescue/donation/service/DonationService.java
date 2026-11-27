package com.foodrescue.donation.service;

import com.foodrescue.common.exception.ApiException;
import com.foodrescue.donation.dto.DonationDtos.DonationForm;
import com.foodrescue.donation.dto.DonationDtos.DonationResponse;
import com.foodrescue.donation.entity.Allergen;
import com.foodrescue.donation.entity.Donation;
import com.foodrescue.donation.entity.DonationStatus;
import com.foodrescue.donation.entity.FoodCategory;
import com.foodrescue.donation.repository.AllergenRepository;
import com.foodrescue.donation.repository.DonationRepository;
import com.foodrescue.donation.repository.FoodCategoryRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository donations;
    private final FoodCategoryRepository categories;
    private final AllergenRepository allergens;
    private final Clock clock;

    @Transactional
    public DonationResponse create(Long donorId, DonationForm form) {
        validateTimes(form);
        FoodCategory category = category(form.categoryId());
        Donation donation = Donation.builder()
                .donorId(donorId)
                .category(category)
                .foodName(form.foodName().trim())
                .description(form.description())
                .quantityTotal(form.quantity())
                .quantityAvailable(form.quantity())
                .unit(form.unit().trim())
                .expiryAt(form.expiryAt())
                .pickupAddress(form.pickupAddress().trim())
                .lat(form.lat())
                .lng(form.lng())
                .pickupFrom(form.pickupFrom())
                .pickupTo(form.pickupTo())
                .storageConditions(form.storageConditions())
                .allergenNotes(form.allergenNotes())
                .allergens(resolveAllergens(form.allergenIds()))
                .status(DonationStatus.AVAILABLE)
                .build();
        donations.save(donation);
        return toResponse(donation);
    }

    @Transactional
    public DonationResponse update(Long donorId, Long id, DonationForm form) {
        Donation donation = ownedForUpdate(donorId, id);
        if (donation.getStatus() != DonationStatus.AVAILABLE) {
            throw ApiException.conflict("DONATION_NOT_EDITABLE", "Only available donations can be edited");
        }
        validateTimes(form);
        BigDecimal allocated = donation.allocatedQuantity();
        if (form.quantity().compareTo(allocated) <= 0) {
            throw ApiException.conflict("QUANTITY_BELOW_ALLOCATED",
                    "Quantity must be greater than the " + allocated + " already allocated to accepted requests");
        }
        donation.setCategory(category(form.categoryId()));
        donation.setFoodName(form.foodName().trim());
        donation.setDescription(form.description());
        donation.setQuantityTotal(form.quantity());
        donation.setQuantityAvailable(form.quantity().subtract(allocated));
        donation.setUnit(form.unit().trim());
        if (!form.expiryAt().equals(donation.getExpiryAt())) {
            donation.setExpiryWarningSent(false);
        }
        donation.setExpiryAt(form.expiryAt());
        donation.setPickupAddress(form.pickupAddress().trim());
        donation.setLat(form.lat());
        donation.setLng(form.lng());
        donation.setPickupFrom(form.pickupFrom());
        donation.setPickupTo(form.pickupTo());
        donation.setStorageConditions(form.storageConditions());
        donation.setAllergenNotes(form.allergenNotes());
        donation.getAllergens().clear();
        donation.getAllergens().addAll(resolveAllergens(form.allergenIds()));
        donations.save(donation);
        return toResponse(donation);
    }

    /** DELETE is a soft delete (status CANCELLED) so request/pickup history keeps valid references. */
    @Transactional
    public void cancel(Long donorId, Long id) {
        Donation donation = ownedForUpdate(donorId, id);
        if (!donation.getStatus().isActive()) {
            throw ApiException.conflict("DONATION_NOT_CANCELLABLE", "Donation is already " + donation.getStatus());
        }
        if (donation.allocatedQuantity().signum() > 0) {
            throw ApiException.conflict("DONATION_HAS_ALLOCATIONS",
                    "Part of this donation is allocated to accepted requests; cancel those requests first");
        }
        donation.transitionTo(DonationStatus.CANCELLED);
        donations.save(donation);
    }

    @Transactional(readOnly = true)
    public List<DonationResponse> mine(Long donorId) {
        return toResponses(donations.findByDonorIdOrderByCreatedAtDesc(donorId));
    }

    @Transactional(readOnly = true)
    public DonationResponse get(Long id) {
        return toResponse(donations.findById(id).orElseThrow(() -> ApiException.notFound("Donation")));
    }


    /** Maps donations to DTOs. Allergens are a lazy collection loaded in batches (@BatchSize), so no N+1. */
    @Transactional(readOnly = true)
    public List<DonationResponse> toResponses(Collection<Donation> list) {
        return list.stream().map(DonationResponse::from).toList();
    }

    private DonationResponse toResponse(Donation donation) {
        return toResponses(List.of(donation)).get(0);
    }

    private Donation ownedForUpdate(Long donorId, Long id) {
        Donation donation = donations.findByIdForUpdate(id).orElseThrow(() -> ApiException.notFound("Donation"));
        if (!donation.getDonorId().equals(donorId)) {
            throw ApiException.forbidden("You can only manage your own donations");
        }
        return donation;
    }

    private Set<Allergen> resolveAllergens(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<>();
        }
        Set<Long> wanted = new HashSet<>(ids);
        List<Allergen> found = allergens.findAllById(wanted);
        if (found.size() != wanted.size()) {
            throw ApiException.badRequest("INVALID_ALLERGEN", "Unknown allergen id in " + wanted);
        }
        return new HashSet<>(found);
    }

    private FoodCategory category(Long id) {
        return categories.findById(id)
                .orElseThrow(() -> ApiException.badRequest("INVALID_CATEGORY", "Unknown food category"));
    }

    private void validateTimes(DonationForm form) {
        Instant now = clock.instant();
        if (!form.expiryAt().isAfter(now)) {
            throw ApiException.badRequest("INVALID_EXPIRY", "Expiry time must be in the future");
        }
        if (!form.pickupFrom().isBefore(form.pickupTo())) {
            throw ApiException.badRequest("INVALID_PICKUP_WINDOW", "Pickup window start must be before its end");
        }
        if (form.pickupTo().isAfter(form.expiryAt())) {
            throw ApiException.badRequest("INVALID_PICKUP_WINDOW", "Pickup window must end before the food expires");
        }
    }
}
