package com.sample.ordermonitor.coreapi.events;

import java.time.Instant;

public record OrderIntakeRejectedEvent(String rxNumber,
                                       String orderIntakeId,
                                       Instant rejectedDate,
                                       String reason) {
}
