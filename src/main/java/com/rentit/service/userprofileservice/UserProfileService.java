package com.rentit.service.userprofileservice;

import com.rentit.entity.user.ProfileImage;
import com.rentit.entity.user.UserEntity;
import com.rentit.entity.user.UserProfileEntity;
import com.rentit.payload.request.user.UserProfileUpdateRequest;
import com.rentit.repository.user.ProfileImageRepository;
import com.rentit.repository.user.UserProfileRepository;
import com.rentit.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;

    private final UserProfileRepository userProfileRepository;

    private final FileService fileService;

    private final ProfileImageRepository profileImageRepository;

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

    public String updateProfileImage(long userId, MultipartFile file) throws IOException {
        UserEntity userEntity = userRepository.findById(userId).
                orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String fileName = fileService.uploadImage(file);

        ProfileImage  profileImage = userEntity.getProfileImage();
        if(profileImage == null) {
            profileImage = new ProfileImage();
            profileImage.setUser(userEntity);
        }
        profileImage.setImageUrl(fileName);

        profileImageRepository.save(profileImage);

        return fileName;
    }
}
