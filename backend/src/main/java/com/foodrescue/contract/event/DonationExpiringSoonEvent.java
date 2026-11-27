package com.foodrescue.contract.event;

import java.time.Instant;

public record DonationExpiringSoonEvent(Long donationId, Long donorId, String foodName,
                                        Instant expiryAt) implements DomainEvent { }
