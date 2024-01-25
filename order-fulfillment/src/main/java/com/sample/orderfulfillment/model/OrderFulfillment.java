package com.sample.orderfulfillment.model;

import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.UUID;

public class OrderFulfillment {

    @Id
    public String id;

    public String rxNumber;

    public String customerName;
    public String insuranceMemberNbr;
    public String shippingAddress;

    public OrderFulfillmentStatus status = OrderFulfillmentStatus.STARTED;

    public Instant receivedDate;
    public Instant packedDate;
    public Instant completedDate;

    public OrderFulfillment(String rxNumber, String customerName, String insuranceMemberNbr, String shippingAddress) {
        this.id = UUID.randomUUID().toString();
        this.rxNumber = rxNumber;
        this.customerName = customerName;
        this.insuranceMemberNbr = insuranceMemberNbr;
        this.shippingAddress = shippingAddress;
        this.receivedDate = Instant.now();
    }

    public void completePacking() {
        this.status = OrderFulfillmentStatus.PACKED;
        this.packedDate = Instant.now();

    }

    public void complete() {
        this.status = OrderFulfillmentStatus.COMPLETE;
        this.completedDate = Instant.now();
    }
}
