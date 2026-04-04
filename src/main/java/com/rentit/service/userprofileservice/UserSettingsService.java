package com.rentit.service.userprofileservice;

import com.rentit.dto.UserSettingsDto;
import com.rentit.entity.user.UserEntity;
import com.rentit.entity.user.UserSettings;
import com.rentit.exception.ResourceNotFoundException;
import com.rentit.repository.user.UserRepository;
import com.rentit.repository.user.UserSettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserSettingsService {

    private final UserRepository userRepository;

    private final UserSettingsRepository userSettingsRepository;

    private UserEntity getUser(Principal principal) {
        log.info("DB call for fetching the user");
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // Get settings, or create default ones if they don't exist yet
    public UserSettingsDto getSettings(Principal principal) {
        UserEntity user = getUser(principal);
        log.info("DB call for fetching the user settings");
        UserSettings settings = userSettingsRepository.findByUser(user)
                .orElseGet(() -> createDefaultSettings(user));
        return mapToDto(settings);
    }

    public UserSettingsDto updateSettings(Principal principal, UserSettingsDto dto) {
        UserEntity user = getUser(principal);
        log.info("DB call for fetching the user settings");
        UserSettings settings = userSettingsRepository.findByUser(user)
                .orElseGet(() -> createDefaultSettings(user));

        settings.setShowEmail(dto.isShowEmail());
        settings.setShowPhone(dto.isShowPhone());
        settings.setAllowMessages(dto.isAllowMessages());
        settings.setEmailAlerts(dto.isEmailAlerts());
        settings.setNewRoomMatches(dto.isNewRoomMatches());
        settings.setPromotionalOffers(dto.isPromotionalOffers());

        log.info("DB call for saving the user settings");
        userSettingsRepository.save(settings);
        return mapToDto(settings);
    }

    private UserSettings createDefaultSettings(UserEntity user) {
        UserSettings settings = new UserSettings();
        settings.setUser(user);
        log.info("DB call for saving the user settings");
        return userSettingsRepository.save(settings);
    }

    private UserSettingsDto mapToDto(UserSettings settings) {
        UserSettingsDto dto = new UserSettingsDto();
        dto.setShowEmail(settings.isShowEmail());
        dto.setShowPhone(settings.isShowPhone());
        dto.setAllowMessages(settings.isAllowMessages());
        dto.setEmailAlerts(settings.isEmailAlerts());
        dto.setNewRoomMatches(settings.isNewRoomMatches());
        dto.setPromotionalOffers(settings.isPromotionalOffers());
        return dto;
    }



}
