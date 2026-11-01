package com.shastage.worker.messaging.kafka;

import com.shastage.worker.domain.event.ProductUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class KafkaProductEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProductEventPublisher.class);

    private final KafkaTemplate<String, ProductUpdateEvent> kafkaTemplate;

    public void send(ProductUpdateEvent event) {
        try {
            SendResult<String, ProductUpdateEvent> result = kafkaTemplate
                    .send("product-updates", event.productId().toString(), event)
                    .get(15, TimeUnit.SECONDS);

            RecordMetadata metadata = result.getRecordMetadata();

            logger.info(
                    "Событие отправлено: eventId={}, productId={}, topic={}, partition={}, offset={}",
                    event.eventId(),
                    event.productId(),
                    metadata.topic(),
                    metadata.partition(),
                    metadata.offset()
            );
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            logger.error("Ожидание отправки прервано", exception);
        } catch (Exception exception) {
            logger.error("Не удалось подтвердить отправку события", exception);
        }
    }
}
