package com.sample.orderfulfillment.repository;

import com.sample.orderfulfillment.model.OrderFulfillment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OrderFulfillmentRepository extends MongoRepository<OrderFulfillment, String> {
    Optional<OrderFulfillment> findByRxNumber(String rxNumber);
}
