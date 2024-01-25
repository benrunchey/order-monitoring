package com.sample.ordermonitor.coreapi.events;

public record OrderShippingDelayedEvent(String rxNumber,
                                        String orderIntakeId) {
}
