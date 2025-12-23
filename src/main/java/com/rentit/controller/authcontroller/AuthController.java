package com.rentit.controller.authcontroller;

import com.rentit.payload.request.LoginRequest;
import com.rentit.payload.request.SignupRequest;
import com.rentit.payload.response.ApiResponse;
import com.rentit.service.authservice.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
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
