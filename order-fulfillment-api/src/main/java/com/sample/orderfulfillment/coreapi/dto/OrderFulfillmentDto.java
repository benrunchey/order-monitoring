package com.sample.orderfulfillment.coreapi.dto;
import java.time.Instant;

public record OrderFulfillmentDto( String id,
                                     String rxNumber,
                                     String customerName,
                                     String insuranceMemberNbr,
                                     String shippingAddress,
                                     OrderFulfillmentStatusDto status,
                                     Instant receivedDate,
                                     Instant packedDate,
                                     Instant completedDate) {
}
