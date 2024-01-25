package com.sample.ordermonitor.coreapi.events;

import java.time.Instant;

public record OrderMonitoringStartedEvent(String rxNumber, String orderIntakeId, String memberNumber,
                                          Instant dateOfService) {
}
