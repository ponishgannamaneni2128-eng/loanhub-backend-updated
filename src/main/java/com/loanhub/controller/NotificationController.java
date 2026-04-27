package com.loanhub.controller;

import com.loanhub.dto.ApiResponse;
import com.loanhub.dto.NotificationDto;
import com.loanhub.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getUserNotifications(
            @PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(notificationService.getNotificationsForUser(userId)));
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getRoleNotifications(
            @PathVariable String role) {
        return ResponseEntity.ok(ApiResponse.ok(notificationService.getNotificationsForRole(role)));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationDto>> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(notificationService.markAsRead(id)));
    }

    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllReadForUser(@PathVariable Long userId) {
        notificationService.markAllReadForUser(userId);
        return ResponseEntity.ok(ApiResponse.ok("All notifications marked as read", null));
    }

    @PutMapping("/role/{role}/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllReadForRole(@PathVariable String role) {
        notificationService.markAllReadForRole(role);
        return ResponseEntity.ok(ApiResponse.ok("All notifications marked as read", null));
    }

    @PutMapping("/{id}/decline")
    public ResponseEntity<ApiResponse<NotificationDto>> declineNotification(
            @PathVariable Long id,
            @RequestBody Map<String, Long> body) {
        Long lenderId = body.get("lenderId");
        return ResponseEntity.ok(ApiResponse.ok(notificationService.declineNotification(id, lenderId)));
    }

    @PutMapping("/loan/{loanId}/accept")
    public ResponseEntity<ApiResponse<NotificationDto>> acceptNotification(@PathVariable Long loanId) {
        return ResponseEntity.ok(ApiResponse.ok(notificationService.acceptNotification(loanId)));
    }
}
