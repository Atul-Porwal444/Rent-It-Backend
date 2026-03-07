package com.rentit.repository.user;

import com.rentit.entity.user.Notification;
import com.rentit.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByCreatedAtDesc(UserEntity user);

    long countByUserAndIsReadFalse(UserEntity user);
}
