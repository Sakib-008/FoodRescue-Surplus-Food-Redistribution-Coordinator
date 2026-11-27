package com.foodrescue.donation.repository;

import com.foodrescue.donation.entity.Donation;
import com.foodrescue.donation.entity.DonationStatus;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DonationRepository extends JpaRepository<Donation, Long>, JpaSpecificationExecutor<Donation> {

    /** SELECT ... FOR UPDATE: serialises concurrent quantity changes on the same donation. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from Donation d where d.id = :id")
    Optional<Donation> findByIdForUpdate(@Param("id") Long id);

    List<Donation> findByDonorIdOrderByCreatedAtDesc(Long donorId);

    boolean existsByCategory_Id(Long categoryId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Donation d set d.status = :expired, d.updatedAt = :now "
            + "where d.status in :active and d.expiryAt <= :now")
    int expireOverdue(@Param("expired") DonationStatus expired,
                      @Param("active") Collection<DonationStatus> active,
                      @Param("now") Instant now);

    @Query("select d from Donation d where d.status in :active and d.expiryAt > :now "
            + "and d.expiryAt <= :threshold and d.expiryWarningSent = false")
    List<Donation> findExpiringSoon(@Param("active") Collection<DonationStatus> active,
                                    @Param("now") Instant now,
                                    @Param("threshold") Instant threshold);

    @Query("select d from Donation d where d.status = :status and d.expiryAt > :now "
            + "and d.pickupTo > :minPickupEnd and d.quantityAvailable > 0")
    List<Donation> findRecommendationCandidates(@Param("status") DonationStatus status,
                                                @Param("now") Instant now,
                                                @Param("minPickupEnd") Instant minPickupEnd);
}
