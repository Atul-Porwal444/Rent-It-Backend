package com.rentit.controller.usercontroller;

import com.rentit.entity.user.UserEntity;
import com.rentit.payload.request.user.UserProfileUpdateRequest;
import com.rentit.payload.response.ApiResponse;
import com.rentit.repository.user.UserRepository;
import com.rentit.service.userprofileservice.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

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

}
