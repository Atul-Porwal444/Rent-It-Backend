package com.rentit.service.userprofileservice;

import com.rentit.entity.user.ProfileImage;
import com.rentit.entity.user.UserEntity;
import com.rentit.entity.user.UserProfileEntity;
import com.rentit.payload.request.user.UserProfileUpdateRequest;
import com.rentit.repository.user.ProfileImageRepository;
import com.rentit.repository.user.UserProfileRepository;
import com.rentit.repository.user.UserRepository;
import com.rentit.service.media.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;

    private final ImageStorageService imageStorageService;

    private final UserProfileRepository userProfileRepository;

    private final ProfileImageRepository profileImageRepository;

    private UserEntity getUserFromPrincipal(Principal principal) {
        String email = principal.getName();
        return userRepository.findByEmail(email).
                orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public void updateUserProfile(Principal principal, UserProfileUpdateRequest userProfileUpdateRequest) {
        UserEntity userEntity = getUserFromPrincipal(principal);

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

    public String updateProfileImage(Principal principal, MultipartFile file) throws IOException {
        UserEntity userEntity = getUserFromPrincipal(principal);

        String publicUrl = imageStorageService.uploadImage(file);

        ProfileImage profileImage = userEntity.getProfileImage();
        if(profileImage == null) {
            profileImage = new ProfileImage();
            profileImage.setUser(userEntity);
        }
        profileImage.setImageUrl(publicUrl);

        profileImageRepository.save(profileImage);

        return publicUrl;
    }
}
