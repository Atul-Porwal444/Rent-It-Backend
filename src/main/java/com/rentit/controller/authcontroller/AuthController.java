package com.rentit.controller.authcontroller;

import com.rentit.payload.request.auth.LoginRequest;
import com.rentit.payload.request.auth.SignupRequest;
import com.rentit.payload.request.auth.VerificationRequest;
import com.rentit.payload.response.ApiResponse;
import com.rentit.service.authservice.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

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
            // Return 200 OK with the Token data
            return ResponseEntity.ok(
                    new ApiResponse(true, "Login Successful", new HashMap<>(){{
                        put("token", token);
                    }})
            );
        } catch (RuntimeException e) {
            // Return 401 UNAUTHORIZED for bad password
            return new ResponseEntity<>(
                    new ApiResponse(false, "Invalid email or password", null),
                    HttpStatus.UNAUTHORIZED
            );
        }
    }
}
