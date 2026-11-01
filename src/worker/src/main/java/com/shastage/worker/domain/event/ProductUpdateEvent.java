package com.shastage.worker.domain.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductUpdateEvent(
        UUID eventId,
        UUID productId,
        BigDecimal price,
        Integer stock,
        Instant occurredAt
) {
}
