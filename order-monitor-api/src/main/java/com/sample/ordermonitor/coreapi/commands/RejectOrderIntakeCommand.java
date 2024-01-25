package com.sample.ordermonitor.coreapi.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.Instant;

public record RejectOrderIntakeCommand(@TargetAggregateIdentifier String rxNumber,
                                       String orderIntakeId,
                                       Instant rejectedDate,
                                       String reason) {
}
