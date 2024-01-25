package com.sample.ordermonitor.coreapi.models;

import org.springframework.data.annotation.Id;

import java.time.Instant;

public record OrderMonitorHistory(@Id String eventId,
                                  String rxNumber,
                                  OrderMonitorStatus status,
                                  Instant eventDate) { }
