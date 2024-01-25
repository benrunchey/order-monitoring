package com.sample.orderintake.model;

import org.springframework.data.annotation.Id;

import java.time.Instant;
import java.util.UUID;

public class OrderIntake {

    @Id
    public String id;

    public String rxNumber;

    public String customerName;
    public String insuranceMemberNbr;
    public Instant dateOfService;
    public String shippingAddress;

    public OrderIntakeStatus status = OrderIntakeStatus.STARTED;

    public Instant receivedDate;
    public Instant approvalDate;
    public Instant rejectedDate;

    public OrderIntake(String rxNumber, String customerName, String insuranceMemberNbr, Instant dateOfService, String shippingAddress) {
        this.id = UUID.randomUUID().toString();;
        this.rxNumber = rxNumber;
        this.customerName = customerName;
        this.insuranceMemberNbr = insuranceMemberNbr;
        this.dateOfService = dateOfService;
        this.shippingAddress = shippingAddress;
        this.receivedDate = Instant.now();
    }

    public void approve() {
        this.status = OrderIntakeStatus.APPROVED;
        this.approvalDate = Instant.now();
        this.rejectedDate = null;
    }

    public void reject() {
        this.status = OrderIntakeStatus.REJECTED;
        this.approvalDate = null;
        this.rejectedDate = Instant.now();
    }

}
