package com.sample.ordermonitor.query;

import com.sample.ordermonitor.coreapi.models.OrderMonitorSummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface OrderMonitorSummaryRepository extends MongoRepository<OrderMonitorSummary, String> {
    Optional<OrderMonitorSummary> findByInsuranceMemberNbrAndDateOfService(String insuranceMemberNbr, Instant dateOfService);
}
