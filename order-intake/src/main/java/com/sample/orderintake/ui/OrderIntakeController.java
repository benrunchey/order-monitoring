package com.sample.orderintake.ui;

import com.sample.orderintake.coreapi.dto.CreateOrderIntakeDto;
import com.sample.orderintake.coreapi.dto.OrderIntakeDto;
import com.sample.orderintake.coreapi.dto.OrderIntakeStatusDto;
import com.sample.orderintake.coreapi.events.OrderIntakeStatusChangedEvent;
import com.sample.orderintake.model.OrderIntake;
import com.sample.orderintake.repository.OrderIntakeRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-intake")
public class OrderIntakeController {

    private final OrderIntakeRepository orderIntakeRepository;

    private final KafkaTemplate<String, OrderIntakeStatusChangedEvent> kafkaTemplate;

    public OrderIntakeController(OrderIntakeRepository orderIntakeRepository, KafkaTemplate<String, OrderIntakeStatusChangedEvent> kafkaTemplate) {
        this.orderIntakeRepository = orderIntakeRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping
    public void createOrderIntake(@RequestBody CreateOrderIntakeDto createOrderIntake) {
        var orderIntake = new OrderIntake(createOrderIntake.rxNumber(),
                                            createOrderIntake.customerName(),
                                            createOrderIntake.insuranceMemberNbr(),
                                            createOrderIntake.dateOfService(),
                                            createOrderIntake.shippingAddress());

        this.orderIntakeRepository.save(orderIntake);
        this.publishEvent(orderIntake);
    }

    @GetMapping
    public List<OrderIntakeDto> getAll() {
        return this.orderIntakeRepository.findAll()
                .stream()
                .map(this::mapToOrderIntakeDto)
                .toList();
    }

    @GetMapping("/{rxNumber}")
    public OrderIntakeDto getByRxNumber(@PathVariable("rxNumber") String rxNumber) {
        var oi = this.orderIntakeRepository.findByRxNumber(rxNumber).orElseThrow();
        return mapToOrderIntakeDto(oi);
    }

    @PatchMapping("/{rxNumber}/approve")
    public void approveOrderIntake(@PathVariable("rxNumber") String rxNumber) {
        var orderIntake = this.orderIntakeRepository.findByRxNumber(rxNumber).orElseThrow();
        orderIntake.approve();
        this.orderIntakeRepository.save(orderIntake);
        this.publishEvent(orderIntake);

    }

    @PatchMapping("/{findByRxNumber}/reject")
    public void rejectOrderIntake(@PathVariable("findByRxNumber") String findByRxNumber) {
        var orderIntake = this.orderIntakeRepository.findByRxNumber(findByRxNumber).orElseThrow();
        orderIntake.reject();
        this.orderIntakeRepository.save(orderIntake);
        this.publishEvent(orderIntake);
    }

    public OrderIntakeDto mapToOrderIntakeDto(OrderIntake oi) {
        return new OrderIntakeDto(oi.id,
                                    oi.rxNumber,
                                    oi.customerName,
                                    oi.insuranceMemberNbr,
                                    oi.dateOfService,
                                    oi.shippingAddress,
                                    OrderIntakeStatusDto.valueOf(oi.status.name()),
                                    oi.receivedDate,
                                    oi.approvalDate,
                                    oi.rejectedDate);
    }

    public void publishEvent(OrderIntake oi) {
        this.kafkaTemplate.send("order-intake", new OrderIntakeStatusChangedEvent(mapToOrderIntakeDto(oi)));
    }
}
