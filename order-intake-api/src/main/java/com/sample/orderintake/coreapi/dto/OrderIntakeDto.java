package com.sample.orderintake.coreapi.dto;

import java.time.Instant;

public record OrderIntakeDto(String id, String rxNumber, String customerName, String insuranceMemberNbr, Instant dateOfService, String shippingAddress,
                             OrderIntakeStatusDto status, Instant receivedDate, Instant approvalDate, Instant rejectedDate) {
}
