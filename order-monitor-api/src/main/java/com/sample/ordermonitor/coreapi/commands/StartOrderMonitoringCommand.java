package com.sample.ordermonitor.coreapi.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.Instant;

public record StartOrderMonitoringCommand(@TargetAggregateIdentifier String rxNumber,
                                          String orderIntakeId,
                                          String memberNumber,
                                          Instant dateOfService) {
}
