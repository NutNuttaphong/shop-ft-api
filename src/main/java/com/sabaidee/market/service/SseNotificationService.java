package com.sabaidee.market.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SseNotificationService {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String username) {
        // Create emitter with 24 hours timeout
        SseEmitter emitter = new SseEmitter(24 * 60 * 60 * 1000L);

        // Save emitter
        emitters.put(username, emitter);
        log.info("User {} connected to SSE. Active subscribers: {}", username, emitters.size());

        emitter.onCompletion(() -> {
            emitters.remove(username);
            log.info("SSE connection completed for user {}.", username);
        });

        emitter.onTimeout(() -> {
            emitters.remove(username);
            log.info("SSE connection timeout for user {}.", username);
        });

        emitter.onError((ex) -> {
            emitters.remove(username);
            log.warn("SSE connection error for user {}: {}", username, ex.getMessage());
        });

        // Send handshake event to establish connection
        try {
            emitter.send(SseEmitter.event()
                    .name("INIT")
                    .data("Connected"));
        } catch (IOException e) {
            emitters.remove(username);
        }

        return emitter;
    }

    public void sendNotification(String username, Object data) {
        sendEvent(username, "NOTIFICATION", data);
    }

    public void sendEvent(String username, String eventName, Object data) {
        SseEmitter emitter = emitters.get(username);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(data));
                log.info("Real-time event {} sent to user: {}", eventName, username);
            } catch (IOException e) {
                emitters.remove(username);
                log.warn("Failed to send SSE event {} to user: {}, connection removed", eventName, username);
            }
        }
    }
}
