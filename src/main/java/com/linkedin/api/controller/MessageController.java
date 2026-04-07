package com.linkedin.api.controller;

import com.linkedin.api.config.CurrentUser;
import com.linkedin.api.document.Conversation;
import com.linkedin.api.document.Message;
import com.linkedin.api.document.*;
import com.linkedin.api.dto.request.MessageRequest;
import com.linkedin.api.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Messaging")
@SecurityRequirement(name = "bearerAuth")
public class MessageController {

    private final MessageService messageService;
    private final CurrentUser currentUser;

    @Operation(summary = "Send a message (creates conversation if needed)")
    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(@Valid @RequestBody MessageRequest.Send req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messageService.sendMessage(currentUser.getId(), req));
    }

    @Operation(summary = "Reply to an existing conversation")
    @PostMapping("/conversations/{convoId}/reply")
    public ResponseEntity<Message> reply(
            @PathVariable String convoId,
            @Valid @RequestBody MessageRequest.Reply req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messageService.replyToConversation(convoId, currentUser.getId(), req));
    }

    @Operation(summary = "Get all messages in a conversation")
    @GetMapping("/conversations/{convoId}")
    public ResponseEntity<List<Message>> getMessages(@PathVariable String convoId) {
        return ResponseEntity.ok(messageService.getMessages(convoId, currentUser.getId()));
    }

    @Operation(summary = "Get all your conversations")
    @GetMapping("/conversations")
    public ResponseEntity<List<Conversation>> getMyConversations() {
        return ResponseEntity.ok(messageService.getMyConversations(currentUser.getId()));
    }

    @Operation(summary = "Get unread message count in a conversation")
    @GetMapping("/conversations/{convoId}/unread")
    public ResponseEntity<Long> getUnreadCount(@PathVariable String convoId) {
        return ResponseEntity.ok(messageService.getUnreadCount(convoId, currentUser.getId()));
    }
}
