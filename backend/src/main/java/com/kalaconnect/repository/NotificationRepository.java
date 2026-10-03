package com.kalaconnect.repository;

import com.kalaconnect.model.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository {

    Notification save(Notification notification);

    Optional<Notification> findById(Long id);

    List<Notification> findByUserId(Long userId, Boolean unreadOnly);

    void markAsRead(Long id, Long userId);

    void markAllAsRead(Long userId);

    int countUnread(Long userId);
}
