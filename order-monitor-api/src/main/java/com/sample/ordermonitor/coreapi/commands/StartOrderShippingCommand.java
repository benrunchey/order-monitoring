package com.sample.ordermonitor.coreapi.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.Instant;

public record StartOrderShippingCommand(@TargetAggregateIdentifier String rxNumber,
                                        String orderShippingId,
                                        Instant startedDate) {
}
