package com.foodrescue.contract.event;

public record PickupAssignedEvent(Long pickupId, Long requestId, Long volunteerId, Long donorId,
                                  Long recipientId, String foodName) implements DomainEvent { }
