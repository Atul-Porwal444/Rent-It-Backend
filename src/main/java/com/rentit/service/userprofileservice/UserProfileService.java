package com.rentit.service.userprofileservice;

import com.rentit.entity.user.UserEntity;
import com.rentit.entity.user.UserProfileEntity;
import com.rentit.payload.request.user.UserProfileUpdateRequest;
import com.rentit.repository.user.UserProfileRepository;
import com.rentit.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;

    private final UserProfileRepository userProfileRepository;

    public void updateUserProfile(Long userId, UserProfileUpdateRequest userProfileUpdateRequest) {
        UserEntity userEntity = userRepository.findById(userId).
                orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserProfileEntity userProfile = userEntity.getProfile();
        if(userProfile == null) {
            userProfile = new UserProfileEntity();
            userProfile.setUser(userEntity);
        }

        userProfile.setLocation(userProfileUpdateRequest.getLocation());
        userProfile.setDob(userProfileUpdateRequest.getDob());
        userProfile.setPhoneNumber(userProfileUpdateRequest.getPhoneNumber());
        userProfile.setGender(userProfileUpdateRequest.getGender());
        userProfile.setOccupation(userProfileUpdateRequest.getOccupation());
        userProfile.setAbout(userProfileUpdateRequest.getAbout());

        userEntity.setProfile(userProfile);
        userRepository.save(userEntity);
    }
}
