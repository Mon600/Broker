package com.shastage.store.infrastructure.websocket;

import com.shastage.store.infrastructure.message.NewProductInfoMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import tools.jackson.databind.json.JsonMapper;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private final JsonMapper jsonMapper = new JsonMapper();

    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);;
        log.info("Новое соединение: " + session.getId());
        session.sendMessage(new TextMessage("Подключено"));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        log.info("Соединение закрыто: " + session.getId());
    }

    public void sendMessage(NewProductInfoMessage message) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(jsonMapper.writeValueAsString(message)));
                } catch (IOException e) {
                    log.error("Failed to send message", e);
                }
            }
        }
    }
}
