package com.rentit.service.authservice;

import com.rentit.entity.user.UserEntity;
import com.rentit.entity.user.UserProfileEntity;
import com.rentit.exception.ResourceNotFoundException;
import com.rentit.payload.request.auth.LoginRequest;
import com.rentit.payload.request.auth.ResetPasswordRequest;
import com.rentit.payload.request.auth.SignupRequest;
import com.rentit.payload.request.auth.VerificationRequest;
import com.rentit.repository.UserAuthProjection;
import com.rentit.repository.user.UserRepository;
import com.rentit.service.EmailService;
import com.rentit.utility.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;

    private final EmailService emailService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final RedisTemplate<String, String> redisTemplate;

    private final String DEFAULT_AVATAR = "https://ui-avatars.com/api/?background=random&name=";

    @Transactional
    public void registerUser(SignupRequest signupRequest) {
        log.info("DB call for fetching the UserAuthProjection");
        if(userRepository.getByEmail(signupRequest.getEmail()).isPresent()){
            throw new RuntimeException("Email Already Exists");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setName(signupRequest.getName());
        userEntity.setTargetCity(signupRequest.getTargetCity());
        userEntity.setEmail(signupRequest.getEmail());
        userEntity.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        userEntity.setProfileImageUrl(DEFAULT_AVATAR + signupRequest.getName());
        userEntity.setVerified(false);

        UserProfileEntity userProfileEntity = new UserProfileEntity();
        userProfileEntity.setLocation("");
        userProfileEntity.setDob("");
        userProfileEntity.setPhone("");
        userProfileEntity.setGender("");
        userProfileEntity.setOccupation("");
        userProfileEntity.setBio("");
        userProfileEntity.setUser(userEntity);
        userEntity.setProfile(userProfileEntity);

        log.info("DB call for saving the user along with the profile data");
        userRepository.save(userEntity);

        String otp = String.valueOf(new Random().nextInt(900000)+100000);

        redisTemplate.opsForValue().set("OTP:"+signupRequest.getEmail(), otp, 10, TimeUnit.MINUTES);

        emailService.sendVerificationEmail(signupRequest.getEmail(), otp);
    }

    public boolean verifyUser(VerificationRequest request) {
        log.info("DB call for verifying the user");
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User with this email not found"));

        if(user.isVerified())
            throw new RuntimeException("User is already verified");

        String storedOtp = redisTemplate.opsForValue().get("OTP:"+request.getEmail());

        if(storedOtp != null && storedOtp.equals(request.getOtp())) {
            user.setVerified(true);
            userRepository.save(user);

            redisTemplate.delete("OTP:"+request.getEmail());
            return true;
        }

        return false;
    }

    public void resendOtp(String email) {
        log.info("DB call for fetching the user");
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if(user.isVerified()) {
            throw new RuntimeException("User is already verified");
        }

        String otp = String.valueOf(new Random().nextInt(900000)+100000);

        redisTemplate.opsForValue().set("OTP:"+email, otp, 10, TimeUnit.MINUTES);

        emailService.sendVerificationEmail(email, otp);
    }

    public String loginUser(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        if (authentication.isAuthenticated()) {

            if(!isAccountVerified(loginRequest.getEmail())) {
                throw new AccessDeniedException("Account is not verified");
            }

            String token = jwtUtil.generateToken(loginRequest.getEmail());
            return token;
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }

    public boolean isAccountVerified(String email) {
        log.info("DB call for fetching the user");
        UserAuthProjection projection = userRepository.getByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with this email not found"));

        return projection.getIsVerified();
    }

    public void processForgotPassword(String email) {
        log.info("DB call for fetching the user");
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with this email not found"));

        String otp = String.valueOf(new Random().nextInt(900000)+100000);

        redisTemplate.opsForValue().set("OTP:"+email, otp, 10, TimeUnit.MINUTES);

        emailService.sendPasswordResetEmail(email, otp);
    }

    public void processResetPassword(ResetPasswordRequest resetPasswordRequest) {
        log.info("DB call for fetching the user");
        UserEntity user = userRepository.findByEmail(resetPasswordRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User with this email not found"));


        String storedOtp = redisTemplate.opsForValue().get("OTP:"+resetPasswordRequest.getEmail());

        if(storedOtp == null)
            throw new ResourceNotFoundException("No password reset request found for this user.");

        if(!storedOtp.equals(resetPasswordRequest.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        log.info("DB call for saving the user");
        userRepository.save(user);

        redisTemplate.delete("OTP:"+resetPasswordRequest.getEmail());
    }

    public void resendForgotPasswordOtp(String email) {
        log.info("DB call for fetching the user");
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with this email not found"));

        String otp = String.valueOf(new Random().nextInt(900000)+100000);

        redisTemplate.opsForValue().set("OTP:"+email, otp, 10, TimeUnit.MINUTES);

        emailService.sendPasswordResetEmail(email, otp);
    }

    public void blacklistJwt(String token) {
        Date expirationDate = jwtUtil.extractExpiration(token);
        long remainingTimeInMillis = expirationDate.getTime() - System.currentTimeMillis();

        if (remainingTimeInMillis > 0) {
            redisTemplate.opsForValue().set(
                    "BLACKLIST:" + token,
                    "logged_out",
                    remainingTimeInMillis,
                    java.util.concurrent.TimeUnit.MILLISECONDS
            );
        }
    }
}
