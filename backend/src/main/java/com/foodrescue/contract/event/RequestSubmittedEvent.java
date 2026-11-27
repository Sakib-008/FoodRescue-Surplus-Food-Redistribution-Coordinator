package com.foodrescue.contract.event;

public record RequestSubmittedEvent(Long requestId, Long donationId, Long donorId, Long recipientId,
                                    String foodName) implements DomainEvent { }
