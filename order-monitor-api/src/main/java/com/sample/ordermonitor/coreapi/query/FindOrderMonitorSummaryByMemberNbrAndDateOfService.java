package com.sample.ordermonitor.coreapi.query;

import java.time.Instant;

public record FindOrderMonitorSummaryByMemberNbrAndDateOfService(String insuranceMemberNbr,
                                                                 Instant dateOfService) {
}
