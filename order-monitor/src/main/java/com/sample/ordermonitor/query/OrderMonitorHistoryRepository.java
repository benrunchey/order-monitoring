package com.sample.ordermonitor.query;

import com.sample.ordermonitor.coreapi.models.OrderMonitorHistory;
import com.sample.ordermonitor.coreapi.models.OrderMonitorSummary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderMonitorHistoryRepository extends MongoRepository<OrderMonitorHistory, String> {
    List<OrderMonitorHistory> findAllByRxNumber(String rxNumber);
}
