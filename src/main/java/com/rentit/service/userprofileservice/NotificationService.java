package com.rentit.service.userprofileservice;

import com.rentit.dto.NotificationDto;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final UserRepository userRepository;

    private final RoomListingRepository roomListingRepository;

    private final NotificationRepository notificationRepository;

    private final RoommateListingRepository roommateListingRepository;

    private final RedisTemplate<String, String> redisTemplate;

    private String getUnreadKey(String email) {
        return "unread_flag:" + email;
    }

    // Helper to send notifications from any class in backend
    @CacheEvict(value = "notifications", key = "#recipient.email")
    public void createNotification(UserEntity recipient, String type, String message) {
        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setType(type);
        notification.setMessage(message);

        log.info("DB call for saving the notification");
        notificationRepository.save(notification);

        redisTemplate.opsForValue().set(getUnreadKey(recipient.getEmail()), "true");
    }

    @Cacheable(value = "notifications", key = "#principal.name")
    public List<NotificationDto> getMyNotifications(Principal principal) {
        log.info("DB call for fetching the user");
        UserEntity user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        log.info("DB call for fetching the notification sorted by created at user");
        return notificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    // Checking if user has any unread notifications
    public boolean hasUnreadNotifications(Principal principal) {
        String email = principal.getName();
        String flag = redisTemplate.opsForValue().get(getUnreadKey(email));

        if(flag == null) {
            log.info("Redis cache miss, now fetching unread notifications from DB");
            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            boolean hasUnread = notificationRepository.countByUserAndIsReadFalse(user) > 0;

            redisTemplate.opsForValue().set(getUnreadKey(email), String.valueOf(hasUnread));
            return hasUnread;
        }

        return Boolean.parseBoolean(flag);
    }

    @CacheEvict(value = "notifications", key = "#principal.name")
    public void markAllAsRead(Principal principal) {
        log.info("DB call for fetching the user");
        UserEntity user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        log.info("DB call for fetching the notification sorted by created at user and filtering unread notifications");
        List<Notification> unread = notificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream().filter(n -> !n.isRead()).collect(Collectors.toList());

        unread.forEach(n -> n.setRead(true));
        log.info("DB call for saving the unread notifications calling the method saveAll");
        notificationRepository.saveAll(unread);

        redisTemplate.opsForValue().set(getUnreadKey(principal.getName()), "false");
    }

    public Boolean contactRoomOwner(Long id, Principal principal) {
        log.info("DB call for fetching the room by id");
        RoomListing post = roomListingRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Room listing not found"));

        log.info("DB call for getting the room owner by calling post.getOwner()");
        UserEntity owner =  post.getUser();
        if(owner != null) {
            createNotification(owner,
                            "alert",
                        "User showed interest in your room post. Contact: " + principal.getName()
            );

            return true;
        }
        return false;
    }

    public Boolean contactRoommateOwner(Long id, Principal principal) {
        log.info("DB call for fetching the roommate by id");
        RoommateListing post = roommateListingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Roommate listing not found"));

        log.info("DB call for getting the room owner by calling post.getOwner()");
        UserEntity owner =  post.getUser();
        if(owner != null) {
            createNotification(owner,
                                "alert",
                                "User showed interest in your roommate post. Contact: " + principal.getName()
            );

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
