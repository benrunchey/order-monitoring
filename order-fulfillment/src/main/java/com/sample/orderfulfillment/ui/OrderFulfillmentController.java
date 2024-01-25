package com.sample.orderfulfillment.ui;


import com.sample.orderfulfillment.coreapi.dto.CreateOrderFulfillmentDto;
import com.sample.orderfulfillment.coreapi.dto.OrderFulfillmentDto;
import com.sample.orderfulfillment.coreapi.dto.OrderFulfillmentStatusDto;
import com.sample.orderfulfillment.coreapi.events.OrderFulfillmentStatusChangedEvent;
import com.sample.orderfulfillment.model.OrderFulfillment;
import com.sample.orderfulfillment.repository.OrderFulfillmentRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-fulfillment")
public class OrderFulfillmentController {
    private final OrderFulfillmentRepository orderFulfillmentRepository;
    private final KafkaTemplate<String, OrderFulfillmentStatusChangedEvent> kafkaTemplate;

    public OrderFulfillmentController(OrderFulfillmentRepository orderFulfillmentRepository, KafkaTemplate<String, OrderFulfillmentStatusChangedEvent> kafkaTemplate) {
        this.orderFulfillmentRepository = orderFulfillmentRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping
    public void createOrderFulfillment(@RequestBody CreateOrderFulfillmentDto createOrderFulfillment) {
        var orderFulfillment = new OrderFulfillment(createOrderFulfillment.rxNumber(),
                createOrderFulfillment.customerName(),
                createOrderFulfillment.insuranceMemberNbr(),
                createOrderFulfillment.shippingAddress());

        this.orderFulfillmentRepository.save(orderFulfillment);
        this.publishEvent(orderFulfillment);
    }

    @GetMapping
    public List<OrderFulfillmentDto> getAll() {
        return this.orderFulfillmentRepository.findAll()
                .stream()
                .map(this::mapToOrderFulfillmentDto)
                .toList();
    }

    @GetMapping("/{rxNumber}")
    public OrderFulfillmentDto getById(@PathVariable("rxNumber") String rxNumber) {
        var of = this.orderFulfillmentRepository.findByRxNumber(rxNumber).orElseThrow();
        return mapToOrderFulfillmentDto(of);
    }

    @PatchMapping("/{rxNumber}/complete-packing")
    public void completePacking(@PathVariable("rxNumber") String rxNumber) {
        var orderFulfillment = this.orderFulfillmentRepository.findByRxNumber(rxNumber).orElseThrow();
        orderFulfillment.completePacking();
        this.orderFulfillmentRepository.save(orderFulfillment);
        this.publishEvent(orderFulfillment);
    }

    @PatchMapping("/{rxNumber}/complete")
    public void complete(@PathVariable("rxNumber") String rxNumber) {
        var orderFulfillment = this.orderFulfillmentRepository.findByRxNumber(rxNumber).orElseThrow();
        orderFulfillment.complete();
        this.orderFulfillmentRepository.save(orderFulfillment);
        this.publishEvent(orderFulfillment);
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
