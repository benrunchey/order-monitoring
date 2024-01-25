package com.sample.ordermonitor.consumer;

import com.sample.orderintake.coreapi.events.OrderIntakeStatusChangedEvent;
import com.sample.ordermonitor.coreapi.commands.ApproveOrderIntakeCommand;
import com.sample.ordermonitor.coreapi.commands.RejectOrderIntakeCommand;
import com.sample.ordermonitor.coreapi.commands.StartOrderMonitoringCommand;
import com.sample.ordermonitor.coreapi.models.OrderMonitorSummary;
import com.sample.ordermonitor.coreapi.query.FindOrderMonitorSummaryByMemberNbrAndDateOfService;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@KafkaListener(topics = "order-intake", groupId="order-monitor", containerFactory = "orderIntakeListenerContainerFactory")
public class OrderIntakeConsumer {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private static final Logger logger = LoggerFactory.getLogger(OrderIntakeConsumer.class);

    public OrderIntakeConsumer(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @KafkaHandler()
    public void orderIntakeStatusEventHandler(@Payload OrderIntakeStatusChangedEvent event) {
        logger.info("Received order intake status message: " + event);

        Object cmd;
        switch (event.orderIntake().status()) {

            case STARTED -> {
                cmd = new StartOrderMonitoringCommand(event.orderIntake().rxNumber(),
                                                        event.orderIntake().id(),
                                                        event.orderIntake().insuranceMemberNbr(),
                                                        event.orderIntake().dateOfService());
            }
            case APPROVED -> {
                cmd = new ApproveOrderIntakeCommand(event.orderIntake().rxNumber(), event.orderIntake().id(), event.orderIntake().approvalDate());
            }
            case REJECTED -> {
                cmd = new RejectOrderIntakeCommand(event.orderIntake().rxNumber(), event.orderIntake().id(), event.orderIntake().approvalDate(), "unknown");
            }
            default -> {
                throw new IllegalStateException(String.format("Unhandled Order-Intake status %s", event.orderIntake().status().name()));
            }
        }

        this.commandGateway.send(cmd);
    }
}
