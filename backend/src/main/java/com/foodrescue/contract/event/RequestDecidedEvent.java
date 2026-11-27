package com.foodrescue.contract.event;

public record RequestDecidedEvent(Long requestId, Long donationId, Long donorId, Long recipientId,
                                  String foodName, Decision decision) implements DomainEvent {
    public enum Decision { ACCEPTED, REJECTED, CANCELLED }
}
