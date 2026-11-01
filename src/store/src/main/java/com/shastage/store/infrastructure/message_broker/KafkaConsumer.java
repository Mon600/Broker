package com.shastage.store.infrastructure.message_broker;

import com.shastage.store.domain.service.ProductService;
import com.shastage.store.domain.service.SseConnectionManager;
import com.shastage.store.infrastructure.mapper.MessageMapper;
import com.shastage.store.infrastructure.message.NewProductInfoMessage;
import com.shastage.store.infrastructure.message_broker.events.ProductEvent;
import com.shastage.store.infrastructure.websocket.WebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ProductService productService;
    private final WebSocketHandler webSocketHandler;
    private final SseConnectionManager connectionManager;

    @KafkaListener(topics = "product-updates", groupId = "product-group")
    public void consume(ProductEvent event) {
        NewProductInfoMessage newInfo = MessageMapper.toNewProductInfoMessage(
                productService.updateProductInfo(
                        event.getProductId(),
                        event.getStock(),
                        event.getPrice())

        );

        webSocketHandler.sendMessage(newInfo);
        connectionManager.broadcast(newInfo);
    }
}