package com.rentit.controller.usercontroller;

import com.rentit.entity.user.UserEntity;
import com.rentit.payload.request.user.UserProfileUpdateRequest;
import com.rentit.payload.response.ApiResponse;
import com.rentit.repository.user.UserRepository;
import com.rentit.service.userprofileservice.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/update")
public class UserProfileController {

    private final UserProfileService userProfileService;

    private final UserRepository userRepository;

    private Long getUserIdFromPrincipal(Principal principal) {
        String email = principal.getName();
        UserEntity userEntity = userRepository.findByEmail(email).
                orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userEntity.getId();
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse> updateUserProfile(@RequestBody UserProfileUpdateRequest userProfileUpdateRequest, Principal principal) {
        Long userId = getUserIdFromPrincipal(principal);
        System.out.println("Enter in controller");
        userProfileService.updateUserProfile(userId, userProfileUpdateRequest);
        return ResponseEntity.ok(new ApiResponse(true, "Profile updated successfully", userProfileUpdateRequest));
    }

    @PostMapping("/profile-image")
    public ResponseEntity<ApiResponse> updateProfileImage(@RequestParam("image") MultipartFile file, Principal principal) {
        try {
            Long userId = getUserIdFromPrincipal(principal);
            String fileName = userProfileService.updateProfileImage(userId,file);

            return ResponseEntity.ok(new ApiResponse(true, "Image uploaded successfully",  new HashMap<>(){{
                put("file",fileName);
            }}));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Image upload failed: " + e.getMessage(), null));
        }
    }

}
