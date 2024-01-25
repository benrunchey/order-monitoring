package com.sample.ordermonitor.query;

import com.sample.ordermonitor.coreapi.events.*;
import com.sample.ordermonitor.coreapi.models.OrderMonitorStatus;
import com.sample.ordermonitor.coreapi.models.OrderMonitorSummary;
import com.sample.ordermonitor.coreapi.query.FindOrderMonitorSummaryById;
import com.sample.ordermonitor.coreapi.query.FindOrderMonitorSummaryByMemberNbrAndDateOfService;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ProcessingGroup("OrderMonitorSummary")
public class OrderMonitorSummaryProjection {

    private final OrderMonitorSummaryRepository orderMonitorSummaryRepository;


    public OrderMonitorSummaryProjection(OrderMonitorSummaryRepository orderMonitorSummaryRepository) {
        this.orderMonitorSummaryRepository = orderMonitorSummaryRepository;
    }

    @EventHandler
    public void on(OrderMonitoringStartedEvent event) {
        var orderMonitorSummary = new OrderMonitorSummary(event.rxNumber(),
                                                            event.memberNumber(),
                                                            event.dateOfService(),
                                                            OrderMonitorStatus.MONITORING_STARTED);
        this.orderMonitorSummaryRepository.save(orderMonitorSummary);
    }

    @EventHandler
    public void on(OrderIntakeStartedEvent event) {
        var summary = this.orderMonitorSummaryRepository.findById(event.rxNumber()).orElseThrow();
        summary.startIntake(event.orderIntakeId());
        this.orderMonitorSummaryRepository.save(summary);
    }

    @EventHandler
    public void on(OrderIntakeDelayedEvent event) {
        var summary = this.orderMonitorSummaryRepository.findById(event.rxNumber()).orElseThrow();
        summary.intakeDelayed();
        this.orderMonitorSummaryRepository.save(summary);
    }

    @EventHandler
    public void on(OrderIntakeApprovedEvent event) {
        var summary = this.orderMonitorSummaryRepository.findById(event.rxNumber()).orElseThrow();
        //summary.approveIntake();
        summary.approveIntake(event.approvedDate());
        this.orderMonitorSummaryRepository.save(summary);

    }

    @EventHandler
    public void on(OrderFulfillmentStartedEvent event) {
        var summary = this.orderMonitorSummaryRepository.findById(event.rxNumber()).orElseThrow();
        summary.acceptFulfillment(event.orderFulfillmentId());
        this.orderMonitorSummaryRepository.save(summary);

    }

    @EventHandler
    public void on(OrderFulfillmentDelayedEvent event) {
        var summary = this.orderMonitorSummaryRepository.findById(event.rxNumber()).orElseThrow();
        summary.fulfillmentDelayed();
        this.orderMonitorSummaryRepository.save(summary);

    }

    @EventHandler
    public void on(OrderFulfillmentPackedEvent event) {
        var summary = this.orderMonitorSummaryRepository.findById(event.rxNumber()).orElseThrow();
        summary.fulfillmentPacking();
        this.orderMonitorSummaryRepository.save(summary);
    }

    @EventHandler
    public void on(OrderFulfillmentCompletedEvent event) {
        var summary = this.orderMonitorSummaryRepository.findById(event.rxNumber()).orElseThrow();
        summary.completeFulfillment();
        this.orderMonitorSummaryRepository.save(summary);
    }

    @QueryHandler
    public OrderMonitorSummary handle(FindOrderMonitorSummaryById query) {
        return this.orderMonitorSummaryRepository.findById(query.rxNumber()).orElseThrow();
    }

    @QueryHandler
    public OrderMonitorSummary handle(FindOrderMonitorSummaryByMemberNbrAndDateOfService query) {
        return this.orderMonitorSummaryRepository.findByInsuranceMemberNbrAndDateOfService(query.insuranceMemberNbr(), query.dateOfService()).orElseThrow();
    }

    @QueryHandler(queryName = "Order-Monitor-Summary-Get-All")
    public List<OrderMonitorSummary> findAll() {
        return this.orderMonitorSummaryRepository.findAll();
    }
}
