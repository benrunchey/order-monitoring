package com.sample.ordermonitor.coreapi.events;

import java.time.Instant;

public record OrderShippingStartedEvent(String rxNumber,
                                        String orderShippingId,
                                        Instant startedDate) {
}
