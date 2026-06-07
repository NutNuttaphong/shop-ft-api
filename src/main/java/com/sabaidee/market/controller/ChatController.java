package com.sabaidee.market.controller;

import com.sabaidee.market.dto.request.ChatMessageRequest;
import com.sabaidee.market.dto.response.ApiResponse;
import com.sabaidee.market.exception.ResourceNotFoundException;
import com.sabaidee.market.model.ChatMessage;
import com.sabaidee.market.model.User;
import com.sabaidee.market.repository.ChatMessageRepository;
import com.sabaidee.market.repository.UserRepository;
import com.sabaidee.market.service.SseNotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final SseNotificationService sseNotificationService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<ChatMessage>> sendMessage(
            @Valid @RequestBody ChatMessageRequest request,
            Principal principal) {

        String senderUsername = principal.getName();
        String receiverUsername = request.getReceiver();

        // Check if receiver exists
        userRepository.findByUsername(receiverUsername)
                .orElseThrow(() -> new ResourceNotFoundException("ไม่พบผู้รับ: " + receiverUsername));

        ChatMessage chatMessage = ChatMessage.builder()
                .sender(senderUsername)
                .receiver(receiverUsername)
                .message(request.getMessage())
                .timestamp(Instant.now())
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        // Send real-time message via SSE to the receiver
        sseNotificationService.sendEvent(receiverUsername, "CHAT_MESSAGE", savedMessage);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(savedMessage));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<ChatMessage>>> getChatHistory(
            @RequestParam String contact,
            Principal principal) {

        String username = principal.getName();
        List<ChatMessage> history = chatMessageRepository.findChatHistory(username, contact);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @GetMapping("/contacts")
    public ResponseEntity<ApiResponse<Set<String>>> getContacts(Principal principal) {
        String username = principal.getName();
        List<ChatMessage> messages = chatMessageRepository.findBySenderOrReceiver(username, username);

        // Extract distinct usernames that are not the current user
        Set<String> contacts = messages.stream()
                .map(msg -> msg.getSender().equals(username) ? msg.getReceiver() : msg.getSender())
                .collect(Collectors.toCollection(TreeSet::new)); // Sorted alphabetically

        return ResponseEntity.ok(ApiResponse.success(contacts));
    }
}
