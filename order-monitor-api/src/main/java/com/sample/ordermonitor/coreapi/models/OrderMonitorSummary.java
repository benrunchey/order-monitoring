package com.sample.ordermonitor.coreapi.models;

import org.springframework.data.annotation.Id;

import java.time.Instant;

public class OrderMonitorSummary{

    @Id
    private String rxNumber;
    private String insuranceMemberNbr;
    private Instant dateOfService;
    private OrderMonitorStatus status;
    private String orderIntakeId;
    private String orderFulfillmentId;
    private String orderShipmentId;

    public OrderMonitorSummary() {
    }

    public OrderMonitorSummary(String rxNumber, String insuranceMemberNbr, Instant dateOfService, OrderMonitorStatus status){
        this.rxNumber = rxNumber;
        this.insuranceMemberNbr = insuranceMemberNbr;
        this.dateOfService = dateOfService;
        this.status = status;
    }

    public String getRxNumber() {
        return rxNumber;
    }

    public String getInsuranceMemberNbr() {return insuranceMemberNbr;}

    public Instant getDateOfService() {return dateOfService;}

    public OrderMonitorStatus getStatus() {
        return status;
    }

    public void setStatus(OrderMonitorStatus status) {
        this.status = status;
    }

    public String getOrderIntakeId() {
        return orderIntakeId;
    }

    public String getOrderFulfillmentId() {
        return orderFulfillmentId;
    }

    public String getOrderShipmentId() {
        return orderShipmentId;
    }

    public void startIntake(String orderIntakeId) {
        this.orderIntakeId = orderIntakeId;
        this.setStatus(OrderMonitorStatus.INTAKE_STARTED);
    }

    public void intakeDelayed() {
        this.setStatus(OrderMonitorStatus.INTAKE_DELAYED);
    }
    public void approveIntake() {
        this.setStatus(OrderMonitorStatus.INTAKE_APPROVED);
    }

    public void rejectIntake() {
        this.setStatus(OrderMonitorStatus.INTAKE_REJECTED);
    }

    public void acceptFulfillment(String orderFulfillmentId) {
        this.orderFulfillmentId = orderFulfillmentId;
        this.setStatus(OrderMonitorStatus.FULFILLMENT_STARTED);
    }

    public void fulfillmentDelayed() {
        this.setStatus(OrderMonitorStatus.FULFILLMENT_DELAYED);
    }
    public void startFulfillment() {
        this.setStatus(OrderMonitorStatus.FULFILLMENT_STARTED);
    }

    public void fulfillmentPacking() {
        this.setStatus(OrderMonitorStatus.FULFILLMENT_PACKED);
    }

    public void completeFulfillment() {
        this.setStatus(OrderMonitorStatus.FULFILLMENT_COMPLETE);
    }




}
