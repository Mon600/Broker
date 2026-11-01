package com.shastage.store.web.controller;

import com.shastage.store.domain.service.SseConnectionManager;
import com.shastage.store.infrastructure.message.NewProductInfoMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("api/v1/sse")
@Slf4j
@AllArgsConstructor
public class SseController {

    private final SseConnectionManager connectionManager;

    @GetMapping("/subscribe")
    public SseEmitter streamEvents(@RequestParam String clientId) {
        return connectionManager.createConnection(clientId);

    }
}
