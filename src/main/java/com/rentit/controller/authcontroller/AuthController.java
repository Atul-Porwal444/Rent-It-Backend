package com.rentit.controller.authcontroller;

import com.rentit.payload.request.auth.*;
import com.rentit.payload.response.ApiResponse;
import com.rentit.service.authservice.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request, HttpServletResponse servletResponse) {
        String token = authService.loginUser(request);

        ResponseCookie responseCookie = ResponseCookie.from("jwt", token)
                .httpOnly(true) // this will only allow browser to access the cookie, prevention from XSS
                .secure(true) // setting true in the production because of https
                .path("/") // cookie is valid for whole app
                .maxAge(24 * 60 * 60) // one day validity
                .sameSite("None") // Allows the cookie to be sent across different domains
                .build();

        servletResponse.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

        return ResponseEntity.ok(new ApiResponse(true, "Login successfully", null));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletResponse servletResponse) {
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();

        servletResponse.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

        return ResponseEntity.ok(new ApiResponse(true, "Logout successfully", null));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        try {
            authService.processForgotPassword(forgotPasswordRequest.getEmail());
            return ResponseEntity.ok(new ApiResponse(true, "Reset code sent to email", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body((new ApiResponse(false, e.getMessage(), null)));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            authService.processResetPassword(request);
            return ResponseEntity.ok(new ApiResponse(true, "Password reset successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PostMapping("/resend-fp-otp")
    public ResponseEntity<?> resendForgotPasswordOtp(@RequestBody Map<String, String> request) {
        String email =  request.get("email");
        if(email == null || email.isEmpty()){
            return ResponseEntity.badRequest().body(Map.of("message", "Email is required"));
        }
        this.authService.resendForgotPasswordOtp(email);
        return ResponseEntity.ok(Map.of("message", "OTP resent successfully"));
    }
}
