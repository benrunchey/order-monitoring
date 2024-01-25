package com.sample.orderfulfillment.coreapi.dto;

public record CreateOrderFulfillmentDto(String rxNumber,
                                        String customerName,
                                        String insuranceMemberNbr,
                                        String shippingAddress) {
}
