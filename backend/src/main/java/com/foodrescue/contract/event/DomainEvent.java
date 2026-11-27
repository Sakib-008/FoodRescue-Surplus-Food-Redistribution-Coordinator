package com.foodrescue.contract.event;

/** Observer-pattern contract: modules publish these via ApplicationEventPublisher; listeners react independently. */
public sealed interface DomainEvent
        permits RequestSubmittedEvent, RequestDecidedEvent, PickupAssignedEvent, PickupDeliveredEvent,
        DonationExpiringSoonEvent { }
