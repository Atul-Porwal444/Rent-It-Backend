package com.rentit.service.userprofileservice;

import com.rentit.dto.NotificationDto;
import com.rentit.entity.user.Notification;
import com.rentit.entity.user.UserEntity;
import com.rentit.exception.ResourceNotFoundException;
import com.rentit.repository.user.NotificationRepository;
import com.rentit.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final UserRepository userRepository;

    private final NotificationRepository notificationRepository;

    // Helper to send notifications from any class in backend
    public void createNotification(UserEntity recipient, String type, String message) {
        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setType(type);
        notification.setMessage(message);
        notificationRepository.save(notification);
    }

    public List<NotificationDto> getMyNotifications(Principal principal) {
        UserEntity user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return notificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public void markAsRead(Long id, Principal principal) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));


        if (notification.getUser().getEmail().equals(principal.getName())) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    public void markAllAsRead(Principal principal) {
        UserEntity user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Notification> unread = notificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream().filter(n -> !n.isRead()).collect(Collectors.toList());

        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    private NotificationDto mapToDto(Notification entity) {
        NotificationDto dto = new NotificationDto();
        dto.setId(entity.getId());
        dto.setType(entity.getType());
        dto.setMessage(entity.getMessage());
        dto.setRead(entity.isRead());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

}
