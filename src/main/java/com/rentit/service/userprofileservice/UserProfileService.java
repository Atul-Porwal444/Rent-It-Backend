package com.rentit.service.userprofileservice;

import com.rentit.entity.user.ProfileImage;
import com.rentit.entity.user.UserEntity;
import com.rentit.entity.user.UserProfileEntity;
import com.rentit.exception.ResourceNotFoundException;
import com.rentit.payload.request.user.PasswordChangeRequest;
import com.rentit.payload.request.user.UserProfileUpdateRequest;
import com.rentit.repository.user.ProfileImageRepository;
import com.rentit.repository.user.UserProfileRepository;
import com.rentit.repository.user.UserRepository;
import com.rentit.service.media.ImageStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ImageStorageService imageStorageService;

    private final UserProfileRepository userProfileRepository;

    private final ProfileImageRepository profileImageRepository;

    private UserEntity getUserFromPrincipal(Principal principal) {
        String email = principal.getName();
        return userRepository.findByEmail(email).
                orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public void changePassword(PasswordChangeRequest passwordChangeRequest, Principal principal) {
        UserEntity user = getUserFromPrincipal(principal);

        System.out.println(user.getPassword() + " " + passwordEncoder.encode(passwordChangeRequest.getOldPassword()));

        if(!passwordEncoder.matches(passwordChangeRequest.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password does not match");
        }

        user.setPassword(passwordEncoder.encode(passwordChangeRequest.getNewPassword()));

        userRepository.save(user);
    }

    public void deleteAccount(Principal principal) {
        UserEntity user = getUserFromPrincipal(principal);
        userRepository.delete(user);
    }


    public void updateUserProfile(Principal principal, UserProfileUpdateRequest request) {
        System.out.println(request.getDob());
        
        UserEntity user = getUserFromPrincipal(principal);
        
        if(request.getName() != null && !request.getName().isEmpty()) user.setName(request.getName());

        UserProfileEntity profile = user.getProfile();
        if(profile == null) {
            profile = new UserProfileEntity();
            profile.setUser(user);
        }

        if(request.getBio() != null && !request.getBio().isEmpty()) profile.setBio(request.getBio());
        if(request.getPhone()!= null && !request.getPhone().isEmpty()) profile.setPhone(request.getPhone());
        if(request.getGender() != null && !request.getGender().isEmpty()) profile.setGender(request.getGender());
        if(request.getLocation() != null && !request.getLocation().isEmpty()) profile.setLocation(request.getLocation());
        if(request.getOccupation() != null && !request.getOccupation().isEmpty()) profile.setOccupation(request.getOccupation());
        if(request.getDob() != null && !request.getDob().isEmpty()) profile.setDob(request.getDob());
        
        user.setProfile(profile);
        
        userRepository.save(user);
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
