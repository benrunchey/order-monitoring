package com.sample.orderfulfillment.coreapi.events;

import com.sample.orderfulfillment.coreapi.dto.OrderFulfillmentDto;

public record OrderFulfillmentStatusChangedEvent(OrderFulfillmentDto orderFulfillment) {
}
