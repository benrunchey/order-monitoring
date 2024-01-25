package com.sample.ordermonitor.query;

import com.sample.ordermonitor.coreapi.events.*;
import com.sample.ordermonitor.coreapi.models.OrderMonitorHistory;
import com.sample.ordermonitor.coreapi.models.OrderMonitorStatus;
import com.sample.ordermonitor.coreapi.query.FindOrderMonitorHistoryByRxNumber;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.messaging.annotation.MessageIdentifier;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class OrderMonitorHistoryProjection {

    private final OrderMonitorHistoryRepository orderMonitorHistoryRepository;
    private final QueryUpdateEmitter updateEmitter;


    public OrderMonitorHistoryProjection(OrderMonitorHistoryRepository orderMonitorHistoryRepository, QueryUpdateEmitter updateEmitter) {
        this.orderMonitorHistoryRepository = orderMonitorHistoryRepository;
        this.updateEmitter = updateEmitter;
    }

    @EventHandler
    public void on(OrderMonitoringStartedEvent event, @MessageIdentifier String eventIdentifier, @Timestamp Instant eventDate) {
        var historyEntry = new OrderMonitorHistory(eventIdentifier, event.rxNumber(), OrderMonitorStatus.MONITORING_STARTED, eventDate );
        this.saveHistoryEntry(historyEntry);
    }

    @EventHandler
    public void on(OrderIntakeStartedEvent event, @MessageIdentifier String eventIdentifier, @Timestamp Instant eventDate) {
        var historyEntry = new OrderMonitorHistory(eventIdentifier, event.rxNumber(), OrderMonitorStatus.INTAKE_STARTED,  eventDate );
        this.saveHistoryEntry(historyEntry);
    }

    @EventHandler
    public void on(OrderIntakeDelayedEvent event, @MessageIdentifier String eventIdentifier, @Timestamp Instant eventDate) {
        var historyEntry = new OrderMonitorHistory(eventIdentifier, event.rxNumber(), OrderMonitorStatus.INTAKE_DELAYED, eventDate );
        this.saveHistoryEntry(historyEntry);
    }

    @EventHandler
    public void on(OrderIntakeApprovedEvent event, @MessageIdentifier String eventIdentifier, @Timestamp Instant eventDate) {
        var historyEntry = new OrderMonitorHistory(eventIdentifier, event.rxNumber(), OrderMonitorStatus.INTAKE_APPROVED, eventDate );
        this.saveHistoryEntry(historyEntry);
    }

    @EventHandler
    public void on(OrderIntakeRejectedEvent event, @MessageIdentifier String eventIdentifier, @Timestamp Instant eventDate) {
        var historyEntry = new OrderMonitorHistory(eventIdentifier, event.rxNumber(), OrderMonitorStatus.INTAKE_REJECTED, eventDate );
        this.saveHistoryEntry(historyEntry);
    }

    @EventHandler
    public void on(OrderFulfillmentStartedEvent event, @MessageIdentifier String eventIdentifier, @Timestamp Instant eventDate) {
        var historyEntry = new OrderMonitorHistory(eventIdentifier, event.rxNumber(), OrderMonitorStatus.FULFILLMENT_STARTED, eventDate );
        this.saveHistoryEntry(historyEntry);
    }

    @EventHandler
    public void on(OrderFulfillmentDelayedEvent event, @MessageIdentifier String eventIdentifier, @Timestamp Instant eventDate) {
        var historyEntry = new OrderMonitorHistory(eventIdentifier, event.rxNumber(), OrderMonitorStatus.FULFILLMENT_DELAYED, eventDate );
        this.saveHistoryEntry(historyEntry);
    }

    @EventHandler
    public void on(OrderFulfillmentPackedEvent event, @MessageIdentifier String eventIdentifier, @Timestamp Instant eventDate) {
        var historyEntry = new OrderMonitorHistory(eventIdentifier, event.rxNumber(), OrderMonitorStatus.FULFILLMENT_PACKED, eventDate );
        this.saveHistoryEntry(historyEntry);
    }

    @EventHandler
    public void on(OrderFulfillmentCompletedEvent event, @MessageIdentifier String eventIdentifier, @Timestamp Instant eventDate) {
        var historyEntry = new OrderMonitorHistory(eventIdentifier, event.rxNumber(), OrderMonitorStatus.FULFILLMENT_COMPLETE, eventDate );
        this.saveHistoryEntry(historyEntry);
    }

    @EventHandler
    public void on(OrderShippingStartedEvent event, @MessageIdentifier String eventIdentifier, @Timestamp Instant eventDate) {
        var historyEntry = new OrderMonitorHistory(eventIdentifier, event.rxNumber(), OrderMonitorStatus.SHIPPING_STARTED, eventDate );
        this.saveHistoryEntry(historyEntry);
    }

    @EventHandler
    public void on(OrderShippingDelayedEvent event, @MessageIdentifier String eventIdentifier, @Timestamp Instant eventDate) {
        var historyEntry = new OrderMonitorHistory(eventIdentifier, event.rxNumber(), OrderMonitorStatus.SHIPPING_DELAYED, eventDate );
        this.saveHistoryEntry(historyEntry);
    }

    @EventHandler
    public void on(OrderShippingToCarrierEvent event, @MessageIdentifier String eventIdentifier, @Timestamp Instant eventDate) {
        var historyEntry = new OrderMonitorHistory(eventIdentifier, event.rxNumber(), OrderMonitorStatus.SHIPPING_OUT_TO_CARRIER, eventDate );
        this.saveHistoryEntry(historyEntry);
    }

    @EventHandler
    public void on(OrderShippingCompletedEvent event, @MessageIdentifier String eventIdentifier, @Timestamp Instant eventDate) {
        var historyEntry = new OrderMonitorHistory(eventIdentifier, event.rxNumber(), OrderMonitorStatus.SHIPPING_COMPLETE, eventDate );
        this.saveHistoryEntry(historyEntry);
    }

    private void saveHistoryEntry(OrderMonitorHistory historyEntry) {
        this.orderMonitorHistoryRepository.save(historyEntry);
        updateEmitter.emit(FindOrderMonitorHistoryByRxNumber.class, query->  query.rxNumber().equals(historyEntry.rxNumber()) , List.of(historyEntry));

    }

    @QueryHandler
    public List<OrderMonitorHistory> handle(FindOrderMonitorHistoryByRxNumber query) {
        return this.orderMonitorHistoryRepository.findAllByRxNumber(query.rxNumber());
    }




}
