package com.sample.ordermonitor.coreapi.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.Instant;

public record ApproveOrderIntakeCommand(@TargetAggregateIdentifier String rxNumber,
                                        String orderIntakeId,
                                        Instant approvedDate ) {
}
