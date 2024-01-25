package com.sample.ordermonitor.coreapi.events;

import java.time.Instant;

public record OrderFulfillmentCompletedEvent(String rxNumber,
                                             String orderFulfillmentId,
                                             Instant completedDate) {
}
