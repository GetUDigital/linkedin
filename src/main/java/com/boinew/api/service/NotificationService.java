package com.boinew.api.service;

import com.boinew.api.document.Notification;
import com.boinew.api.repository.mongo.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Page<Notification> getNotifications(Long userId, int page, int size) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(
                userId, PageRequest.of(page, size));
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    public void markAllRead(Long userId) {
        Page<Notification> notifs = notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, 200));
        notifs.getContent().forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifs.getContent());
    }

    public void clearRead(Long userId) {
        notificationRepository.deleteByUserIdAndIsReadTrue(userId);
    }

    // Called internally by other services to push a notification
    public Notification push(Long userId, Long actorId,
                              Notification.NotificationType type, String refId) {
        Notification notif = Notification.builder()
                .userId(userId)
                .actorId(actorId)
                .type(type)
                .refId(refId)
                .isRead(false)
                .build();
        return notificationRepository.save(notif);
    }
}
