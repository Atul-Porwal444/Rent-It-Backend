package com.rentit.service.authservice;

import com.rentit.entity.auth.VerificationToken;
import com.rentit.entity.user.UserEntity;
import com.rentit.entity.user.UserProfileEntity;
import com.rentit.exception.ResourceNotFoundException;
import com.rentit.payload.request.auth.LoginRequest;
import com.rentit.payload.request.auth.ResetPasswordRequest;
import com.rentit.payload.request.auth.SignupRequest;
import com.rentit.payload.request.auth.VerificationRequest;
import com.rentit.repository.UserAuthProjection;
import com.rentit.repository.auth.VerificationTokenRepository;
import com.rentit.repository.user.UserRepository;
import com.rentit.service.EmailService;
import com.rentit.utility.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;

    private final EmailService emailService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final VerificationTokenRepository verificationTokenRepository;

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
        userEntity.setProfileImageUrl(DEFAULT_AVATAR);
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

        VerificationToken token = new VerificationToken();
        token.setOtp(otp);
        token.setUser(userEntity);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        log.info("DB call for saving the OTP");
        verificationTokenRepository.save(token);

        emailService.sendVerificationEmail(signupRequest.getEmail(), otp);
    }

    public boolean verifyUser(VerificationRequest request) {
        log.info("DB call for verifying the user");
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User with this email not found"));

        log.info("DB call for fetching the OTP from DB");
        VerificationToken token = verificationTokenRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("No account verification request found for this user"));

        if(token.getOtp().equals(request.getOtp()) && token.getExpiryDate().isAfter(LocalDateTime.now())) {
            user.setVerified(true);
            log.info("DB call for saving the user");
            userRepository.save(user);
            log.info("DB call for deleting the OTP");
            verificationTokenRepository.delete(token);
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

        log.info("DB call for fetching the OTP");
        VerificationToken token = verificationTokenRepository.findByUser(user)
                .orElse(null);

        if(token == null) {
            token = new VerificationToken();
            token.setUser(user);
        }

        token.setOtp(otp);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        log.info("DB call for saving the OTP");
        verificationTokenRepository.save(token);

        emailService.sendVerificationEmail(email, otp);
    }

    public String loginUser(LoginRequest loginRequest) {

        if(!isAccountVerified(loginRequest.getEmail())) {
            throw new AccessDeniedException("Account is not verified");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        if (authentication.isAuthenticated()) {
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

        log.info("DB call for fetching the OTP");
        VerificationToken token = verificationTokenRepository.findByUser(user)
                .orElse(new VerificationToken());

        token.setUser(user);
        token.setOtp(otp);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        log.info("DB call for saving the OTP");
        verificationTokenRepository.save(token);

        emailService.sendPasswordResetEmail(email, otp);
    }

    public void processResetPassword(ResetPasswordRequest resetPasswordRequest) {
        log.info("DB call for fetching the user");
        UserEntity user = userRepository.findByEmail(resetPasswordRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User with this email not found"));

        log.info("DB call for fetching the OTP");
        VerificationToken token = verificationTokenRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("No password reset request found for this user."));

        if(!token.getOtp().equals(resetPasswordRequest.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        if(token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired. Please request a new one.");
        }

        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        log.info("DB call for saving the user");
        userRepository.save(user);

        token.setOtp(null);
        token.setExpiryDate(null);
        log.info("DB call for saving the OTP");
        verificationTokenRepository.save(token);
    }

    public void resendForgotPasswordOtp(String email) {
        log.info("DB call for fetching the user");
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with this email not found"));

        String otp = String.valueOf(new Random().nextInt(900000)+100000);

        log.info("DB call for fetching the OTP");
        VerificationToken token = verificationTokenRepository.findByUser(user)
                .orElse(null);

        if(token == null) {
            token = new VerificationToken();
            token.setUser(user);
        }

        token.setOtp(otp);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        log.info("DB call for saving the OTP");
        verificationTokenRepository.save(token);

        emailService.sendPasswordResetEmail(email, otp);
    }
}
