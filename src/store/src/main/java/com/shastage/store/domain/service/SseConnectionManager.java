package com.shastage.store.domain.service;

import com.shastage.store.infrastructure.message.NewProductInfoMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SseConnectionManager {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private static final Long DEFAULT_TIMEOUT = 60L * 60L * 1000L;

    public SseEmitter createConnection(String clientId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitters.put(clientId, emitter);

        emitter.onCompletion(() -> {
            log.info("Client {} disconnected", clientId);
            emitters.remove(clientId);
        });

        emitter.onTimeout(() -> {
            log.warn("Client {} timed out", clientId);
            emitters.remove(clientId);
        });

        return emitter;
    }


    public void broadcast(NewProductInfoMessage message) {
        List<String> clientForRemoval = new ArrayList<>();
        for (Map.Entry<String, SseEmitter> entry : emitters.entrySet()) {
            String clientId = entry.getKey();
            SseEmitter emitter = entry.getValue();

            try {
                emitter.send(message);
            } catch (Exception e) {
                log.error("Error sending message to client {}: {}", clientId, e.getMessage());
                clientForRemoval.add(clientId);
            }
        }
        clientForRemoval.forEach(emitters::remove);
    }

    public void removeConnection(String clientId) {
        SseEmitter emitter = emitters.remove(clientId);
        if (emitter != null) {
            emitter.complete();
        }
    }

}


/*

 */
