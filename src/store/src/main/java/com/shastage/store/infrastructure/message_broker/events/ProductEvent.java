package com.shastage.store.infrastructure.message_broker.events;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class ProductEvent {
    private final UUID eventId;
    private final UUID productId;
    private BigDecimal price;
    private int stock;
    private Instant occurredAt;
}