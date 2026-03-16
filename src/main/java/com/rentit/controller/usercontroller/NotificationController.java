package com.rentit.controller.usercontroller;

import com.rentit.dto.NotificationDto;
import com.rentit.service.userprofileservice.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notification")
    public ResponseEntity<List<NotificationDto>> getNotifications(Principal principal) {
        return ResponseEntity.ok(notificationService.getMyNotifications(principal));
    }

    @GetMapping("/has-unread")
    public ResponseEntity<Boolean> hasUnreadNotifications(Principal principal) {
        return ResponseEntity.ok(notificationService.hasUnreadNotifications(principal));
    }

    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(Principal principal) {
        notificationService.markAllAsRead(principal);
        return ResponseEntity.ok(Map.of("success", true));
    }

}
