package com.foodrescue.contract.event;

public record PickupDeliveredEvent(Long pickupId, Long requestId, Long volunteerId, Long donorId,
                                   Long recipientId, String foodName) implements DomainEvent { }
