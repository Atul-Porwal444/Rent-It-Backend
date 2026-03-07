package com.rentit.repository.user;

import com.rentit.entity.user.UserEntity;
import com.rentit.entity.user.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Integer> {
    Optional<UserSettings> findByUser(UserEntity user);
}
