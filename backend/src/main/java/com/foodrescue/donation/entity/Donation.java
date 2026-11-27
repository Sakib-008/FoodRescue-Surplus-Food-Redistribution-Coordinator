package com.foodrescue.donation.entity;

import com.foodrescue.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

@Entity
@Table(name = "donations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Donation extends BaseEntity {

    @Column(name = "donor_id", nullable = false)
    private Long donorId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private FoodCategory category;

    @Column(name = "food_name", nullable = false)
    private String foodName;

    @Column(length = 2000)
    private String description;

    @Column(name = "quantity_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal quantityTotal;

    @Column(name = "quantity_available", nullable = false, precision = 12, scale = 2)
    private BigDecimal quantityAvailable;

    @Column(nullable = false, length = 20)
    private String unit;

    @Column(name = "expiry_at", nullable = false)
    private Instant expiryAt;

    @Column(name = "pickup_address", nullable = false, length = 500)
    private String pickupAddress;

    private Double lat;
    private Double lng;

    @Column(name = "pickup_from", nullable = false)
    private Instant pickupFrom;

    @Column(name = "pickup_to", nullable = false)
    private Instant pickupTo;

    @Column(name = "storage_conditions", length = 500)
    private String storageConditions;

    /** Free-text allergen information not covered by the standard list. */
    @Column(name = "allergen_notes", length = 500)
    private String allergenNotes;

    /** M : M - standard allergens contained in this donation (join table donation_allergens). */
    @ManyToMany
    @JoinTable(name = "donation_allergens",
            joinColumns = @JoinColumn(name = "donation_id"),
            inverseJoinColumns = @JoinColumn(name = "allergen_id"))
    @BatchSize(size = 50)
    @Builder.Default
    private Set<Allergen> allergens = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DonationStatus status;

    @Column(name = "expiry_warning_sent", nullable = false)
    @Builder.Default
    private boolean expiryWarningSent = false;

    public void transitionTo(DonationStatus next) {
        this.status = this.status.requireTransitionTo(next);
    }

    public boolean isExpiredAt(Instant now) {
        return !expiryAt.isAfter(now);
    }

    public BigDecimal allocatedQuantity() {
        return quantityTotal.subtract(quantityAvailable);
    }
}
