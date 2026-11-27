package com.foodrescue.expiry.service;

import com.foodrescue.contract.event.DonationExpiringSoonEvent;
import com.foodrescue.donation.entity.Donation;
import com.foodrescue.donation.entity.DonationStatus;
import com.foodrescue.donation.repository.DonationRepository;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpiryService {

    public record ExpiryResult(int expired, int warned) { }

    private static final EnumSet<DonationStatus> ACTIVE = EnumSet.of(DonationStatus.AVAILABLE, DonationStatus.RESERVED);

    private final DonationRepository donations;
    private final ApplicationEventPublisher publisher;
    private final Clock clock;
    private final long warningHours;

    public ExpiryService(DonationRepository donations, ApplicationEventPublisher publisher, Clock clock,
                         @Value("${app.expiry.warning-hours:2}") long warningHours) {
        this.donations = donations;
        this.publisher = publisher;
        this.clock = clock;
        this.warningHours = warningHours;
    }

    /** Expires overdue donations, then warns about the ones about to expire (each donation is warned once). */
    @Transactional
    public ExpiryResult runCycle() {
        Instant now = clock.instant();
        int expired = donations.expireOverdue(DonationStatus.EXPIRED, ACTIVE, now);

        List<Donation> soon = donations.findExpiringSoon(ACTIVE, now, now.plus(Duration.ofHours(warningHours)));
        for (Donation d : soon) {
            publisher.publishEvent(new DonationExpiringSoonEvent(d.getId(), d.getDonorId(), d.getFoodName(), d.getExpiryAt()));
            d.setExpiryWarningSent(true);
        }
        donations.saveAll(soon);
        return new ExpiryResult(expired, soon.size());
    }
}
