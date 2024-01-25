package com.sample.orderintake.repository;

import com.sample.orderintake.model.OrderIntake;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface OrderIntakeRepository extends MongoRepository<OrderIntake, String> {
    Optional<OrderIntake> findByRxNumber(String rxNumber);
}
