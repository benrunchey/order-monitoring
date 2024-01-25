package com.sample.ordermonitor.coreapi.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.Instant;

public record StartOrderFulfillmentCommand(@TargetAggregateIdentifier String rxNumber,
                                           String orderFulfillmentId,
                                           Instant startedDate ) {
}
