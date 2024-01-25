package com.sample.ordermonitor.coreapi.events;

import java.time.Instant;

public record OrderShippingCompletedEvent(String rxNumber,
                                         String orderShippingId,
                                         Instant completedDate) {
}
