package com.sample.ordermonitor.coreapi.events;

import java.time.Instant;

public record OrderShippingToCarrierEvent(String rxNumber,
                                          String orderShippingId,
                                          Instant sentDate) {
}
