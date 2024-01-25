package com.sample.ordermonitor.coreapi.events;

public record OrderIntakeStartedEvent(String rxNumber,
                                      String orderIntakeId){
}
