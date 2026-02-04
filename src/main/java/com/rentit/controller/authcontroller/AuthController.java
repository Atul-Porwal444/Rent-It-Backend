package com.rentit.controller.authcontroller;

import com.rentit.entity.user.UserEntity;
import com.rentit.entity.user.UserProfileEntity;
import com.rentit.exception.ResourceNotFoundException;
import com.rentit.payload.request.auth.LoginRequest;
import com.rentit.payload.request.auth.SignupRequest;
import com.rentit.payload.request.auth.VerificationRequest;
import com.rentit.payload.response.ApiResponse;
import com.rentit.payload.response.LoginResponse;
import com.rentit.repository.user.UserRepository;
import com.rentit.service.authservice.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("signup")
    public ResponseEntity<ApiResponse> signupUser(@RequestBody SignupRequest signupRequest) {
        try {
            authService.registerUser(signupRequest);
            return new ResponseEntity<>(
                    new ApiResponse(true, "User registered successfully", null),
                    HttpStatus.CREATED
            );
        } catch (RuntimeException e) {
            return new ResponseEntity<>(
                    new ApiResponse(false, e.getMessage(), null),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyUser(@RequestBody VerificationRequest verificationRequest) {
        if(this.authService.verifyUser(verificationRequest)){
            return ResponseEntity.ok(new ApiResponse(true, "User verified successfully", null));
        }
        return new ResponseEntity<>(
                new ApiResponse(false, "Otp is Invalid or Expired", null),
                HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@RequestBody Map<String, String> request) {
        String email =  request.get("email");

        if(email == null || email.isEmpty()){
            return ResponseEntity.badRequest().body(Map.of("message", "Email is required"));
        }

        this.authService.resendOtp(email);

        return ResponseEntity.ok(Map.of("message", "OTP resent successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {

        if(!authService.isAccountVerified(request.getEmail())) {
            return ResponseEntity.status(403).body(new ApiResponse(
                    false,
                    "Unverified account",
                    null
            ));
        }

        try {
            String token = authService.loginUser(request);

            UserEntity user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            final LoginResponse response = getLoginResponse(user, token);
            // Return 200 OK with the Token data
            return ResponseEntity.ok(
                    new ApiResponse(true, "Login Successful", response)
            );
        } catch (RuntimeException e) {
            // Return 401 UNAUTHORIZED for bad password
            return new ResponseEntity<>(
                    new ApiResponse(false, "Invalid email or password", null),
                    HttpStatus.UNAUTHORIZED
            );
        }
    }

    private static LoginResponse getLoginResponse(UserEntity user, String token) {
        UserProfileEntity profile = user.getProfile();

        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setToken(token);
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setProfileUrl(user.getProfileImage().getImageUrl());
        response.setLocation(profile.getLocation());
        response.setDob(profile.getDob());
        response.setPhone(profile.getPhone());
        response.setGender(profile.getGender());
        response.setOccupation(profile.getOccupation());
        response.setBio(profile.getBio());
        return response;
    }
}
