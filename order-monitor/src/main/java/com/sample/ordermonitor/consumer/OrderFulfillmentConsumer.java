package com.sample.ordermonitor.consumer;

import com.sample.orderfulfillment.coreapi.events.OrderFulfillmentStatusChangedEvent;
import com.sample.orderintake.coreapi.events.OrderIntakeStatusChangedEvent;
import com.sample.ordermonitor.coreapi.commands.*;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "order-fulfillment", groupId="order-monitor", containerFactory = "orderFulfillmentListenerContainerFactory")
public class OrderFulfillmentConsumer {

    private final CommandGateway commandGateway;
    private static final Logger logger = LoggerFactory.getLogger(OrderFulfillmentConsumer.class);

    public OrderFulfillmentConsumer(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @KafkaHandler()
    public void orderIntakeStatusEventHandler(@Payload OrderFulfillmentStatusChangedEvent event) {
        logger.info("Received order fullfillment status message: " + event);

        Object cmd;
        switch (event.orderFulfillment().status()) {

            case STARTED -> {
                cmd = new StartOrderFulfillmentCommand(event.orderFulfillment().rxNumber(),
                        event.orderFulfillment().id(),
                        event.orderFulfillment().receivedDate());
            }
            case PACKED -> {
                cmd = new PackOrderFulfillmentCommand(event.orderFulfillment().rxNumber(), event.orderFulfillment().id(), event.orderFulfillment().packedDate());
            }
            case COMPLETE -> {
                cmd = new CompleteOrderFulfillmentCommand(event.orderFulfillment().rxNumber(), event.orderFulfillment().id(), event.orderFulfillment().completedDate());
            }
            default -> {
                throw new IllegalStateException(String.format("Unhandled Order-Fulfillment status %s", event.orderFulfillment().status().name()));
            }
        }

        this.commandGateway.send(cmd);
    }
}

