package com.kalaconnect.service;

import com.kalaconnect.model.Notification;
import com.kalaconnect.model.User;
import com.kalaconnect.repository.NotificationRepository;
import com.kalaconnect.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    private Optional<User> getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return Optional.empty();
        }
        return userRepository.findByEmail(auth.getName());
    }

    @Transactional
    public Notification createNotification(Long userId, String title, String message, String type) {
        if (userId == null) return null;
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type != null ? type : "SYSTEM");
        notification.setRead(false);
        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<Notification> getMyNotifications(Boolean unreadOnly) {
        return getAuthenticatedUser()
                .map(user -> notificationRepository.findByUserId(user.getId(), unreadOnly))
                .orElse(Collections.emptyList());
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        getAuthenticatedUser().ifPresent(user -> {
            notificationRepository.markAsRead(notificationId, user.getId());
        });
    }

    @Transactional
    public void markAllAsRead() {
        getAuthenticatedUser().ifPresent(user -> {
            notificationRepository.markAllAsRead(user.getId());
        });
    }

    @Transactional(readOnly = true)
    public int getUnreadCount() {
        return getAuthenticatedUser()
                .map(user -> notificationRepository.countUnread(user.getId()))
                .orElse(0);
    }
}
