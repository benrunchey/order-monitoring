package com.sample.orderintake.coreapi.events;

import com.sample.orderintake.coreapi.dto.OrderIntakeDto;

public record OrderIntakeStatusChangedEvent(OrderIntakeDto orderIntake) {
}
