package com.sample.ordermonitor.coreapi.events;

public record OrderIntakeDelayedEvent(String rxNumber,
                                      String orderIntakeId) {
}
