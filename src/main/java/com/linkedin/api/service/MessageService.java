package com.linkedin.api.service;

import com.linkedin.api.document.Conversation;
import com.linkedin.api.document.Message;
import com.linkedin.api.dto.request.MessageRequest;
import com.linkedin.api.exception.ResourceNotFoundException;
import com.linkedin.api.exception.UnauthorizedException;
import com.linkedin.api.repository.mongo.ConversationRepository;
import com.linkedin.api.repository.mongo.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public Message sendMessage(Long senderId, MessageRequest.Send req) {
        // Find or create a direct conversation between the two users
        Conversation convo = conversationRepository
                .findDirectConversation(senderId, req.getRecipientId())
                .orElseGet(() -> {
                    Conversation c = Conversation.builder()
                            .memberIds(Arrays.asList(senderId, req.getRecipientId()))
                            .createdAt(LocalDateTime.now())
                            .lastMessageAt(LocalDateTime.now())
                            .build();
                    return conversationRepository.save(c);
                });

        Message msg = Message.builder()
                .convoId(convo.getConvoId())
                .senderId(senderId)
                .content(req.getContent())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        convo.setLastMessageAt(LocalDateTime.now());
        conversationRepository.save(convo);

        return messageRepository.save(msg);
    }

    public Message replyToConversation(String convoId, Long senderId, MessageRequest.Reply req) {
        Conversation convo = conversationRepository.findById(convoId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found: " + convoId));

        if (!convo.getMemberIds().contains(senderId)) {
            throw new UnauthorizedException("You are not a member of this conversation");
        }

        Message msg = Message.builder()
                .convoId(convoId)
                .senderId(senderId)
                .content(req.getContent())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        convo.setLastMessageAt(LocalDateTime.now());
        conversationRepository.save(convo);

        return messageRepository.save(msg);
    }

    public List<Message> getMessages(String convoId, Long currentUserId) {
        Conversation convo = conversationRepository.findById(convoId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found: " + convoId));

        if (!convo.getMemberIds().contains(currentUserId)) {
            throw new UnauthorizedException("You are not a member of this conversation");
        }

        return messageRepository.findByConvoIdOrderByCreatedAtAsc(convoId);
    }

    public List<Conversation> getMyConversations(Long userId) {
        return conversationRepository.findByMemberId(userId);
    }

    public long getUnreadCount(String convoId, Long userId) {
        return messageRepository.countByConvoIdAndIsReadFalseAndSenderIdNot(convoId, userId);
    }
}
