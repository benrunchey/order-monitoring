package com.sample.ordermonitor.command;

import org.axonframework.deadline.DeadlineManager;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class OrderMonitorDeadlineManager {

    private DeadlineManager deadlineManager;

    private static final int DEADLINE_TIMEOUT_SECONDS = 30;

    public static final String INTAKE_STARTED = "intakeStarted";
    public static final String ORDER_INTAKE_STARTED_DEADLINE_ID = "orderIntakeStartedDeadlineId";

    public static final String FULFILLMENT_STARTED = "fulfillmentStarted";
    public static final String ORDER_FULFILLMENT_STARTED_DEADLINE_ID = "orderFulfillmentStartedDeadlineId";

    public static final String SHIPPING_STARTED = "shippingStarted";
    public static final String ORDER_SHIPPING_STARTED_DEADLINE_ID = "orderShippingStartedDeadlineId";

    public OrderMonitorDeadlineManager(DeadlineManager deadlineManager) {
        this.deadlineManager = deadlineManager;
    }

    public DeadlineSchedule scheduleOrderIntakeDeadline(String orderIntakeId) {
        String deadlineId = deadlineManager.schedule(Duration.ofSeconds(DEADLINE_TIMEOUT_SECONDS), INTAKE_STARTED, orderIntakeId);
        return new DeadlineSchedule(ORDER_INTAKE_STARTED_DEADLINE_ID, deadlineId);
    }

    public void cancelOrderIntakeDeadline(String orderIntakeStartedDeadlineId) {
        this.cancelDeadline(INTAKE_STARTED, orderIntakeStartedDeadlineId);
    }

    public DeadlineSchedule scheduleOrderFulfillmentDeadline(String orderFulfillmentId) {
        String deadlineId = deadlineManager.schedule(Duration.ofSeconds(DEADLINE_TIMEOUT_SECONDS), FULFILLMENT_STARTED, orderFulfillmentId);
        return new DeadlineSchedule(ORDER_FULFILLMENT_STARTED_DEADLINE_ID, deadlineId);
    }

    public void cancelOrderFulfillmentDeadline(String orderFulfillmentStartedDeadlineId) {
        this.cancelDeadline(FULFILLMENT_STARTED, orderFulfillmentStartedDeadlineId);
    }

    public DeadlineSchedule scheduleOrderShippingDeadline(String orderShippingId) {
        String deadlineId = deadlineManager.schedule(Duration.ofSeconds(DEADLINE_TIMEOUT_SECONDS), SHIPPING_STARTED, orderShippingId);
        return new DeadlineSchedule(ORDER_SHIPPING_STARTED_DEADLINE_ID, deadlineId);
    }

    public void cancelOrderShippingDeadline(String orderShippingStartedDeadlineId) {
        this.cancelDeadline(SHIPPING_STARTED, orderShippingStartedDeadlineId);
    }

    private void cancelDeadline(String deadlineName, String deadlineId) {
        deadlineManager.cancelSchedule(deadlineName, deadlineId);
    }


}
