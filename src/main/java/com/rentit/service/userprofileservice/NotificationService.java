package com.rentit.service.userprofileservice;

import com.rentit.dto.NotificationDto;
import com.rentit.entity.post.BaseListing;
import com.rentit.entity.post.RoomListing;
import com.rentit.entity.post.RoommateListing;
import com.rentit.entity.user.Notification;
import com.rentit.entity.user.UserEntity;
import com.rentit.exception.ResourceNotFoundException;
import com.rentit.repository.post.RoomListingRepository;
import com.rentit.repository.post.RoommateListingRepository;
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

    private final RoomListingRepository roomListingRepository;

    private final NotificationRepository notificationRepository;

    private final RoommateListingRepository roommateListingRepository;

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

    // Checking if user has any unread notifications
    public boolean hasUnreadNotifications(Principal principal) {
        UserEntity user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return notificationRepository.countByUserAndIsReadFalse(user) > 0;
    }

    public void markAllAsRead(Principal principal) {
        UserEntity user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Notification> unread = notificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream().filter(n -> !n.isRead()).collect(Collectors.toList());

        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    public Boolean contactRoomOwner(Long id, Principal principal) {
        RoomListing post = roomListingRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Room listing not found"));

        UserEntity owner =  post.getUser();
        if(owner != null) {
            Notification notification = new Notification();
            notification.setUser(owner);
            notification.setType("alert");
            notification.setMessage("User showed interest in your room post. Contact: " + principal.getName());
            notificationRepository.save(notification);
            return true;
        }
        return false;
    }

    public Boolean contactRoommateOwner(Long id, Principal principal) {
        RoommateListing post = roommateListingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Roommate listing not found"));

        UserEntity owner =  post.getUser();
        if(owner != null) {
            Notification notification = new Notification();
            notification.setUser(owner);
            notification.setType("alert");
            notification.setMessage("User showed interest in your roommate post. Contact: " + principal.getName());
            notificationRepository.save(notification);
            return true;
        }
        return false;
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
