package com.sample.ordermonitor.ui;

import com.sample.ordermonitor.coreapi.commands.ApproveOrderIntakeCommand;
import com.sample.ordermonitor.coreapi.commands.RejectOrderIntakeCommand;
import com.sample.ordermonitor.coreapi.commands.StartOrderFulfillmentCommand;
import com.sample.ordermonitor.coreapi.commands.StartOrderMonitoringCommand;
import com.sample.ordermonitor.coreapi.models.OrderMonitorHistory;
import com.sample.ordermonitor.coreapi.models.OrderMonitorSummary;
import com.sample.ordermonitor.coreapi.query.FindOrderMonitorHistoryByRxNumber;
import com.sample.ordermonitor.coreapi.query.FindOrderMonitorSummaryById;
import io.grpc.Server;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.SubscriptionQueryResult;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/order-monitor")
public class OrderMonitorController {

    private final CommandGateway commandGateway;

    private final QueryGateway queryGateway;

    public OrderMonitorController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping
    public CompletableFuture<Void> startOrderMonitor(@RequestParam("rxNumber") String rxNumber,
                                                        @RequestParam("orderIntakeId") String orderIntakeId) {
        return commandGateway.send(new StartOrderMonitoringCommand(rxNumber,
                                                                    orderIntakeId,
                                                                    UUID.randomUUID().toString(),
                                                                    Instant.now()));
    }

    @PatchMapping("/{rxNumber}/order-intake/{orderIntakeId}/approve")
    public CompletableFuture<Void> approveOrderFulfillment(@PathVariable("rxNumber") String rxNumber,
                                                         @PathVariable("orderIntakeId") String orderIntakeId) {
        return commandGateway.send(new ApproveOrderIntakeCommand(rxNumber, orderIntakeId, Instant.now()));
    }

    @PatchMapping("/{rxNumber}/order-intake/{orderIntakeId}/reject")
    public CompletableFuture<Void> rejectOrderFulfillment(@PathVariable("rxNumber") String rxNumber,
                                                         @PathVariable("orderIntakeId") String orderIntakeId) {
        return commandGateway.send(new RejectOrderIntakeCommand(rxNumber,
                                                                    orderIntakeId,
                                                                    Instant.now(),
                                                                    "Service date outside coverage"));
    }

    @PatchMapping("/{rxNumber}/order-fulfillment/{orderFulfillmentId}")
    public CompletableFuture<Void> startOrderFulfillment(@PathVariable("rxNumber") String rxNumber,
                                                         @PathVariable("orderFulfillmentId") String orderFulfillmentId) {
        return commandGateway.send(new StartOrderFulfillmentCommand(rxNumber, orderFulfillmentId, Instant.now()));
    }

    @GetMapping("/")
    public CompletableFuture<List<OrderMonitorSummary>> getById() {
        return queryGateway.query("Order-Monitor-Summary-Get-All", null, ResponseTypes.multipleInstancesOf(OrderMonitorSummary.class));
    }


    @GetMapping("/{rxNumber}")
    public CompletableFuture<OrderMonitorSummary> getById(@PathVariable("rxNumber") String rxNumber) {
        return queryGateway.query(new FindOrderMonitorSummaryById(rxNumber), ResponseTypes.instanceOf(OrderMonitorSummary.class));
    }

    @GetMapping(value = "watch/{rxNumber}")
    public Flux<ServerSentEvent<List<OrderMonitorHistory>>> watchOrderMonitor(@PathVariable("rxNumber") String rxNumber) {
        var query = new FindOrderMonitorHistoryByRxNumber(rxNumber);
        SubscriptionQueryResult<List<OrderMonitorHistory>, List<OrderMonitorHistory>> subscriptionQuery =
                queryGateway.subscriptionQuery(query, ResponseTypes.multipleInstancesOf(OrderMonitorHistory.class), ResponseTypes.multipleInstancesOf(OrderMonitorHistory.class));
        return subscriptionQuery
                .initialResult()
                .concatWith(subscriptionQuery.updates())
                .map( history ->
                        ServerSentEvent.<List<OrderMonitorHistory>>builder(history).event("message").build());
    }

}
