package com.sample.ordermonitor.coreapi.events;

import java.time.Instant;

public record OrderFulfillmentStartedEvent(String rxNumber,
                                           String orderFulfillmentId,
                                           Instant receivedDate) {
}
