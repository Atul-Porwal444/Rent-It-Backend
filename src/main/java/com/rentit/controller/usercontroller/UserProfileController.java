package com.rentit.controller.usercontroller;

import com.rentit.payload.request.user.PasswordChangeRequest;
import com.rentit.payload.request.user.UserProfileUpdateRequest;
import com.rentit.payload.response.ApiResponse;
import com.rentit.repository.user.UserRepository;
import com.rentit.service.userprofileservice.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse> updateUserProfile(@RequestBody UserProfileUpdateRequest userProfileUpdateRequest, Principal principal) {
        userProfileService.updateUserProfile(principal, userProfileUpdateRequest);
        return ResponseEntity.ok(new ApiResponse(true, "Profile updated successfully", userProfileUpdateRequest));
    }

    @PostMapping("/profile-image")
    public ResponseEntity<ApiResponse> updateProfileImage(@RequestParam("image") MultipartFile file, Principal principal) {
        try {
            String publicUrl = userProfileService.updateProfileImage(principal,file);

            return ResponseEntity.ok(new ApiResponse(true, "Image uploaded successfully",  new HashMap<>(){{
                put("url",publicUrl);
            }}));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Image upload failed: " + e.getMessage(), null));
        }
    }

    @PostMapping("/password")
    public ResponseEntity<ApiResponse> changePassword(@RequestBody PasswordChangeRequest passwordChangeRequest, Principal principal) {
        try {
            userProfileService.changePassword(passwordChangeRequest, principal);
        } catch (Exception e) {
            return ResponseEntity.status(422).body(new ApiResponse(false, "Password change failed: " + e.getMessage(), null));
        }
        return ResponseEntity.ok(new ApiResponse(true, "Password changed successfully", passwordChangeRequest));
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<ApiResponse> deleteAccount(Principal principal) {
        this.userProfileService.deleteAccount(principal);
        return ResponseEntity.ok(new ApiResponse(true, "Account deleted successfully", null));
    }

}
