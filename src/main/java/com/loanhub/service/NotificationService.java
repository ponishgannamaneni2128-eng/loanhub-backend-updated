package com.loanhub.service;

import com.loanhub.dto.NotificationDto;
import com.loanhub.entity.Notification;
import com.loanhub.entity.User;
import com.loanhub.exception.AppException;
import com.loanhub.repository.NotificationRepository;
import com.loanhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired private NotificationRepository notificationRepository;
    @Autowired private UserRepository userRepository;

    public List<NotificationDto> getNotificationsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found"));
        return notificationRepository.findByRecipient(user).stream()
                .map(NotificationDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<NotificationDto> getNotificationsForRole(String role) {
        return notificationRepository.findByTargetRole(role).stream()
                .filter(n -> !n.isAccepted())
                .map(NotificationDto::fromEntity)
                .collect(Collectors.toList());
    }

    public NotificationDto markAsRead(Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new AppException("Notification not found"));
        n.setRead(true);
        n.setReadAt(LocalDateTime.now());
        return NotificationDto.fromEntity(notificationRepository.save(n));
    }

    public void markAllReadForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found"));
        List<Notification> unread = notificationRepository.findByRecipientAndIsRead(user, false);
        unread.forEach(n -> { n.setRead(true); n.setReadAt(LocalDateTime.now()); });
        notificationRepository.saveAll(unread);
    }

    public void markAllReadForRole(String role) {
        List<Notification> notifs = notificationRepository.findByTargetRole(role);
        notifs.forEach(n -> { n.setRead(true); n.setReadAt(LocalDateTime.now()); });
        notificationRepository.saveAll(notifs);
    }

    public NotificationDto declineNotification(Long id, Long lenderId) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new AppException("Notification not found"));
        String declined = n.getDeclinedBy() != null ? n.getDeclinedBy() + "," + lenderId : String.valueOf(lenderId);
        n.setDeclinedBy(declined);
        n.setRead(true);
        return NotificationDto.fromEntity(notificationRepository.save(n));
    }

    public NotificationDto acceptNotification(Long loanId) {
        List<Notification> notifs = notificationRepository.findByLoanId(loanId);
        notifs.forEach(n -> { n.setAccepted(true); n.setRead(true); });
        notificationRepository.saveAll(notifs);
        return notifs.isEmpty() ? null : NotificationDto.fromEntity(notifs.get(0));
    }
}
