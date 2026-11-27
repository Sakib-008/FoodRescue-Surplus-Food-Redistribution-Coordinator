package com.foodrescue.donation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.foodrescue.donation.entity.Allergen;
import com.foodrescue.donation.entity.Donation;
import com.foodrescue.donation.entity.DonationStatus;
import com.foodrescue.donation.entity.FoodCategory;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

public final class DonationDtos {
    private DonationDtos() { }

    public record DonationForm(
            @NotBlank @Size(max = 150) String foodName,
            @NotNull Long categoryId,
            @Size(max = 2000) String description,
            @NotNull @DecimalMin(value = "0.01", message = "must be greater than 0") @Digits(integer = 10, fraction = 2) BigDecimal quantity,
            @NotBlank @Size(max = 20) String unit,
            @NotNull @Future(message = "must be in the future") Instant expiryAt,
            @NotBlank @Size(max = 500) String pickupAddress,
            @DecimalMin("-90.0") @DecimalMax("90.0") Double lat,
            @DecimalMin("-180.0") @DecimalMax("180.0") Double lng,
            @NotNull Instant pickupFrom,
            @NotNull Instant pickupTo,
            @Size(max = 500) String storageConditions,
            @Size(max = 20) List<Long> allergenIds,
            @Size(max = 500) String allergenNotes) { }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record DonationResponse(Long id, Long donorId, Long categoryId, String categoryName, String foodName,
                                   String description, BigDecimal quantityTotal, BigDecimal quantityAvailable,
                                   String unit, Instant expiryAt, String pickupAddress, Double lat, Double lng,
                                   Instant pickupFrom, Instant pickupTo, String storageConditions,
                                   List<AllergenDto> allergens, String allergenNotes, DonationStatus status, Double distanceKm,
                                   Double matchScore, Instant createdAt) {

        public static DonationResponse from(Donation d) {
            List<AllergenDto> allergens = d.getAllergens().stream().map(AllergenDto::from)
                    .sorted(Comparator.comparing(AllergenDto::name)).toList();
            return new DonationResponse(d.getId(), d.getDonorId(), d.getCategory().getId(), d.getCategory().getName(),
                    d.getFoodName(), d.getDescription(), d.getQuantityTotal(), d.getQuantityAvailable(), d.getUnit(),
                    d.getExpiryAt(), d.getPickupAddress(), d.getLat(), d.getLng(), d.getPickupFrom(), d.getPickupTo(),
                    d.getStorageConditions(), allergens, d.getAllergenNotes(), d.getStatus(), null, null,
                    d.getCreatedAt());
        }

        public DonationResponse withDistanceKm(Double km) {
            return new DonationResponse(id, donorId, categoryId, categoryName, foodName, description, quantityTotal,
                    quantityAvailable, unit, expiryAt, pickupAddress, lat, lng, pickupFrom, pickupTo,
                    storageConditions, allergens, allergenNotes, status, km, matchScore, createdAt);
        }

        public DonationResponse withMatchScore(Double score) {
            return new DonationResponse(id, donorId, categoryId, categoryName, foodName, description, quantityTotal,
                    quantityAvailable, unit, expiryAt, pickupAddress, lat, lng, pickupFrom, pickupTo,
                    storageConditions, allergens, allergenNotes, status, distanceKm, score, createdAt);
        }
    }

    public record CategoryDto(Long id, String name) {
        public static CategoryDto from(FoodCategory c) {
            return new CategoryDto(c.getId(), c.getName());
        }
    }

    public record CategoryForm(@NotBlank @Size(max = 80) String name) { }

    public record AllergenDto(Long id, String name) {
        public static AllergenDto from(Allergen a) {
            return new AllergenDto(a.getId(), a.getName());
        }
    }
}
