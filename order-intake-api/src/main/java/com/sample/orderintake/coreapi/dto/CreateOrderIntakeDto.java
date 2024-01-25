package com.sample.orderintake.coreapi.dto;

import java.time.Instant;

public record CreateOrderIntakeDto(String rxNumber,
                                   String customerName,
                                   String insuranceMemberNbr,
                                   Instant dateOfService,
                                   String shippingAddress) {
}
