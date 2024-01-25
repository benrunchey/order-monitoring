package com.sample.ordermonitor.command;

import com.sample.ordermonitor.coreapi.commands.*;
import com.sample.ordermonitor.coreapi.events.*;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.messaging.MetaData;
import org.axonframework.messaging.annotation.MetaDataValue;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class OrderMonitor {

    @AggregateIdentifier
    private String rxNumber;

    private String orderIntakeStartedDeadlineId;

    private String orderFulfillmentStartedDeadlineId;

    private String orderShippingStartedDeadlineId;

    private OrderMonitorStatus status;

    public OrderMonitor() {
    }

    @CommandHandler
    public OrderMonitor(StartOrderMonitoringCommand command, OrderMonitorDeadlineManager deadlineManager) {
        apply(new OrderMonitoringStartedEvent(command.rxNumber(), command.orderIntakeId(), command.memberNumber(), command.dateOfService()));

        var orderIntakeStartedDeadline = deadlineManager.scheduleOrderIntakeDeadline(command.orderIntakeId());

        apply(new OrderIntakeStartedEvent(command.rxNumber(), command.orderIntakeId()),
                MetaData.with(orderIntakeStartedDeadline.deadlineName(),
                                orderIntakeStartedDeadline.deadlineId()));
    }

    //*** Intake Commands
    @CommandHandler
    public void handle(ApproveOrderIntakeCommand command, OrderMonitorDeadlineManager deadlineManager) {
        if (this.status == OrderMonitorStatus.INTAKE_APPROVED){
            return;
        }

        if (this.status != OrderMonitorStatus.INTAKE_STARTED &&
            this.status != OrderMonitorStatus.INTAKE_DELAYED) {
            throw createIllegalStateTransitionEx(OrderMonitorStatus.INTAKE_APPROVED);
        }

        deadlineManager.cancelOrderIntakeDeadline(this.orderIntakeStartedDeadlineId);

        apply(new OrderIntakeApprovedEvent(command.rxNumber(), command.orderIntakeId(), command.approvedDate()));
    }

    @CommandHandler
    public void handle(RejectOrderIntakeCommand command, OrderMonitorDeadlineManager deadlineManager) {
        if (this.status != OrderMonitorStatus.INTAKE_STARTED &&
                this.status != OrderMonitorStatus.INTAKE_DELAYED) {
            throw createIllegalStateTransitionEx(OrderMonitorStatus.INTAKE_REJECTED);
        }
        deadlineManager.cancelOrderIntakeDeadline(this.orderIntakeStartedDeadlineId);

        apply(new OrderIntakeRejectedEvent(command.rxNumber(), command.orderIntakeId(), command.rejectedDate(), command.reason()));
    }

    //*** Fulfillment Commands
    @CommandHandler
    public void handle(StartOrderFulfillmentCommand command, OrderMonitorDeadlineManager deadlineManager) {
        if (this.status != OrderMonitorStatus.INTAKE_APPROVED) {
            throw createIllegalStateTransitionEx(OrderMonitorStatus.FULFILLMENT_STARTED);
        }

        var orderFulfillmentStartedDeadline = deadlineManager.scheduleOrderFulfillmentDeadline(command.orderFulfillmentId());

        apply(new OrderFulfillmentStartedEvent(command.rxNumber(), command.orderFulfillmentId(), command.startedDate()),
                MetaData.with(orderFulfillmentStartedDeadline.deadlineName(),
                        orderFulfillmentStartedDeadline.deadlineId()));
    }

    @CommandHandler
    public void handle(PackOrderFulfillmentCommand command, OrderMonitorDeadlineManager deadlineManager) {
        if (this.status != OrderMonitorStatus.FULFILLMENT_STARTED &&
             this.status != OrderMonitorStatus.FULFILLMENT_DELAYED) {
            throw createIllegalStateTransitionEx(OrderMonitorStatus.FULFILLMENT_PACKED);
        }

        deadlineManager.cancelOrderFulfillmentDeadline(this.orderFulfillmentStartedDeadlineId);

        apply(new OrderFulfillmentPackedEvent(command.rxNumber(), command.orderFulfillmentId(), command.packingDate()));
    }

    @CommandHandler
    public void handle(CompleteOrderFulfillmentCommand command) {
        if (this.status != OrderMonitorStatus.FULFILLMENT_PACKED) {
            throw createIllegalStateTransitionEx(OrderMonitorStatus.FULFILLMENT_COMPLETE);
        }

        apply(new OrderFulfillmentCompletedEvent(command.rxNumber(), command.orderFulfillmentId(), command.completedDate()));
    }

    //** Shipping Command Handlers
    @CommandHandler
    public void handle(StartOrderShippingCommand command, OrderMonitorDeadlineManager deadlineManager) {
        if (this.status != OrderMonitorStatus.FULFILLMENT_COMPLETE) {
            throw createIllegalStateTransitionEx(OrderMonitorStatus.SHIPPING_STARTED);
        }

        var orderShippingStartedDeadline = deadlineManager.scheduleOrderShippingDeadline(command.orderShippingId());

        apply(new OrderFulfillmentStartedEvent(command.rxNumber(), command.orderShippingId(), command.startedDate()),
                MetaData.with(orderShippingStartedDeadline.deadlineName(),
                        orderShippingStartedDeadline.deadlineId()));
    }

    @CommandHandler
    public void handle(OutToCarrierOrderShippingCommand command, OrderMonitorDeadlineManager deadlineManager) {
        if (this.status != OrderMonitorStatus.SHIPPING_STARTED &&
                this.status != OrderMonitorStatus.SHIPPING_DELAYED) {
            throw createIllegalStateTransitionEx(OrderMonitorStatus.SHIPPING_OUT_TO_CARRIER);
        }

        deadlineManager.cancelOrderShippingDeadline(this.orderShippingStartedDeadlineId);

        apply(new OrderShippingToCarrierEvent(command.rxNumber(), command.orderShippingId(), command.sentDate()));
    }

    @CommandHandler
    public void handle(CompleteOrderShippingCommand command) {
        if (this.status != OrderMonitorStatus.FULFILLMENT_PACKED) {
            throw createIllegalStateTransitionEx(OrderMonitorStatus.FULFILLMENT_COMPLETE);
        }

        apply(new OrderFulfillmentCompletedEvent(command.rxNumber(), command.orderShippingId(), command.completedDate()));
    }


    @DeadlineHandler(deadlineName=OrderMonitorDeadlineManager.INTAKE_STARTED)
    public void handleOrderIntakeStartedExpired(String orderIntakeId){
        apply(new OrderIntakeDelayedEvent(this.rxNumber, orderIntakeId));
    }

    @DeadlineHandler(deadlineName=OrderMonitorDeadlineManager.FULFILLMENT_STARTED)
    public void handleOrderFulfillmentStartedExpired(String orderFulfillmentId){
        apply(new OrderFulfillmentDelayedEvent(this.rxNumber, orderFulfillmentId));
    }

    @DeadlineHandler(deadlineName=OrderMonitorDeadlineManager.SHIPPING_STARTED)
    public void handleOrderShippingStartedExpired(String orderShippingId){
        apply(new OrderShippingDelayedEvent(this.rxNumber, orderShippingId));
    }

    @EventSourcingHandler
    public void handle(OrderMonitoringStartedEvent event) {
        this.rxNumber = event.rxNumber();
    }

    @EventSourcingHandler
    public void handle (OrderIntakeStartedEvent event,
                        @MetaDataValue(OrderMonitorDeadlineManager.ORDER_INTAKE_STARTED_DEADLINE_ID) String orderIntakeStartedDeadlineId) {
        this.status = OrderMonitorStatus.INTAKE_STARTED;
        this.orderIntakeStartedDeadlineId = orderIntakeStartedDeadlineId;
    }

    @EventSourcingHandler
    public void handle (OrderIntakeDelayedEvent event) {
        this.status = OrderMonitorStatus.INTAKE_DELAYED;
    }

    @EventSourcingHandler
    public void handle (OrderIntakeApprovedEvent event) {
        this.status = OrderMonitorStatus.INTAKE_APPROVED;
    }

    @EventSourcingHandler
    public void handle (OrderIntakeRejectedEvent event) {
        this.status = OrderMonitorStatus.INTAKE_REJECTED;
    }

    @EventSourcingHandler
    public void handle (OrderFulfillmentStartedEvent event, @MetaDataValue(OrderMonitorDeadlineManager.ORDER_FULFILLMENT_STARTED_DEADLINE_ID) String orderFulfillmentStartedDeadlineId) {
        this.status = OrderMonitorStatus.FULFILLMENT_STARTED;
        this.orderFulfillmentStartedDeadlineId = orderFulfillmentStartedDeadlineId;
    }

    @EventSourcingHandler
    public void handle (OrderFulfillmentDelayedEvent event) {
        this.status = OrderMonitorStatus.FULFILLMENT_DELAYED;
    }

    @EventSourcingHandler
    public void handle (OrderFulfillmentPackedEvent event) {
        this.status = OrderMonitorStatus.FULFILLMENT_PACKED;
    }

    @EventSourcingHandler
    public void handle (OrderFulfillmentCompletedEvent event) {
        this.status = OrderMonitorStatus.FULFILLMENT_COMPLETE;
    }


    @EventSourcingHandler
    public void handle (OrderShippingStartedEvent event, @MetaDataValue(OrderMonitorDeadlineManager.ORDER_SHIPPING_STARTED_DEADLINE_ID) String orderShippingStartedDeadlineId) {
        this.status = OrderMonitorStatus.INTAKE_STARTED;
        this.orderShippingStartedDeadlineId = orderShippingStartedDeadlineId;
    }

    @EventSourcingHandler
    public void handle (OrderShippingDelayedEvent event) {
        this.status = OrderMonitorStatus.SHIPPING_DELAYED;
    }

    @EventSourcingHandler
    public void handle (OrderShippingToCarrierEvent event) {this.status = OrderMonitorStatus.SHIPPING_OUT_TO_CARRIER; }

    @EventSourcingHandler
    public void handle (OrderShippingCompletedEvent event) {
        this.status = OrderMonitorStatus.SHIPPING_COMPLETE;
    }


    private IllegalStateException createIllegalStateTransitionEx(OrderMonitorStatus targetState) {
        return new IllegalStateException(String.format("Cannot set status to %s", targetState));
    }

}
