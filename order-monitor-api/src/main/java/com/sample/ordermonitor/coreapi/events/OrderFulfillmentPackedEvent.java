package com.sample.ordermonitor.coreapi.events;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.Instant;

public record OrderFulfillmentPackedEvent(String rxNumber,
                                          String orderFulfillmentId,
                                          Instant packingDate) {
}
