package com.sample.ordermonitor.coreapi.events;

public record OrderFulfillmentDelayedEvent(String rxNumber,
                                           String orderFulfillmentId) {
}
