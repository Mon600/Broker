package com.shastage.worker.domain.service;

import com.shastage.worker.datasource.entity.ProductEntity;
import com.shastage.worker.datasource.repository.jpa.ProductJpaRepository;
import com.shastage.worker.domain.event.ProductUpdateEvent;
import com.shastage.worker.messaging.kafka.KafkaProductEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class ProductEventGenerator {

    private final KafkaProductEventPublisher kafkaProductEventPublisher;
    private final ProductJpaRepository productJpaRepository;

    @Scheduled(
            fixedDelayString = "${worker.interval-ms}",
            initialDelayString = "${worker.interval-ms}"
    )
    public void generate() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        Optional<ProductEntity> randomProduct = productJpaRepository.findRandomProduct();

        if (randomProduct.isEmpty()) {
            return;
        }

        UUID randomProductId = randomProduct.get().getId();
        BigDecimal randomPrice = BigDecimal.valueOf(
                random.nextLong(0, 100_000),
                2
        );
        Integer randomAvailableStock = random.nextInt(0, 50);

        ProductUpdateEvent event = new ProductUpdateEvent(
                UUID.randomUUID(),
                randomProductId,
                randomPrice,
                randomAvailableStock,
                Instant.now()
        );

        kafkaProductEventPublisher.send(event);
    }
}
