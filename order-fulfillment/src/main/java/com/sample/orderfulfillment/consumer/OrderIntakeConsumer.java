package com.sample.orderfulfillment.consumer;


import com.sample.orderfulfillment.coreapi.dto.OrderFulfillmentDto;
import com.sample.orderfulfillment.coreapi.dto.OrderFulfillmentStatusDto;
import com.sample.orderfulfillment.coreapi.events.OrderFulfillmentStatusChangedEvent;
import com.sample.orderfulfillment.model.OrderFulfillment;
import com.sample.orderfulfillment.repository.OrderFulfillmentRepository;
import com.sample.orderintake.coreapi.dto.OrderIntakeStatusDto;
import com.sample.orderintake.coreapi.events.OrderIntakeStatusChangedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "order-intake", groupId="order-fulfillment", containerFactory = "orderIntakeListenerContainerFactory")
public class OrderIntakeConsumer {

    private final OrderFulfillmentRepository orderFulfillmentRepository;
    private final KafkaTemplate<String, OrderFulfillmentStatusChangedEvent> kafkaTemplate;

    private static final Logger logger = LoggerFactory.getLogger(OrderIntakeConsumer.class);

    public OrderIntakeConsumer(OrderFulfillmentRepository repository, KafkaTemplate<String, OrderFulfillmentStatusChangedEvent> kafkaTemplate) {
        this.orderFulfillmentRepository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaHandler()
    public void orderIntakeStatusEventHandler(@Payload OrderIntakeStatusChangedEvent event) {
        logger.info("Received order intake status message: " + event);

        if (event.orderIntake().status() == OrderIntakeStatusDto.APPROVED) {
            logger.info("Creating Order fulfillment: " + event);
            var of = new OrderFulfillment(event.orderIntake().rxNumber(), event.orderIntake().customerName(), event.orderIntake().insuranceMemberNbr(), event.orderIntake().shippingAddress());
            this.orderFulfillmentRepository.save(of);
            this.publishEvent(of);
        }
    }

    private OrderFulfillmentDto mapToOrderFulfillmentDto(OrderFulfillment of) {
        return new OrderFulfillmentDto(of.id,
                of.rxNumber,
                of.customerName,
                of.insuranceMemberNbr,
                of.shippingAddress,
                OrderFulfillmentStatusDto.valueOf(of.status.name()),
                of.receivedDate,
                of.packedDate,
                of.completedDate);
    }

    private void publishEvent(OrderFulfillment of){
        this.kafkaTemplate.send("order-fulfillment", new OrderFulfillmentStatusChangedEvent(mapToOrderFulfillmentDto(of)));
    }
}
