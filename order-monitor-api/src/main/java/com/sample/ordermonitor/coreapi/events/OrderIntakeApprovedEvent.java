package com.sample.ordermonitor.coreapi.events;

import java.time.Instant;

public record OrderIntakeApprovedEvent(String rxNumber,
                                       String orderIntakeId,
                                       Instant approvedDate) {
}
