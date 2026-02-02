package com.rentit.service.authservice;

import com.rentit.entity.auth.VerificationToken;
import com.rentit.entity.user.ProfileImage;
import com.rentit.entity.user.UserEntity;
import com.rentit.entity.user.UserProfileEntity;
import com.rentit.exception.ResourceNotFoundException;
import com.rentit.payload.request.auth.LoginRequest;
import com.rentit.payload.request.auth.SignupRequest;
import com.rentit.payload.request.auth.VerificationRequest;
import com.rentit.repository.auth.VerificationTokenRepository;
import com.rentit.repository.user.UserRepository;
import com.rentit.service.EmailService;
import com.rentit.utility.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;

    private final EmailService emailService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final VerificationTokenRepository verificationTokenRepository;

    private final String DEFAULT_AVATAR = "https://ui-avatars.com/api/?background=random&name=User";

    public void registerUser(SignupRequest signupRequest) {
        if(userRepository.findByEmail(signupRequest.getEmail()).isPresent()){
            throw new RuntimeException("Email Already Exists");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setName(signupRequest.getName());
        userEntity.setEmail(signupRequest.getEmail());
        userEntity.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        userEntity.setVerified(false);

        UserProfileEntity userProfileEntity = new UserProfileEntity();
        userProfileEntity.setLocation("");
        userProfileEntity.setDob("");
        userProfileEntity.setPhoneNumber("");
        userProfileEntity.setGender("");
        userProfileEntity.setOccupation("");
        userProfileEntity.setAbout("");
        userProfileEntity.setUser(userEntity);
        userEntity.setProfile(userProfileEntity);

        ProfileImage profileImage = new ProfileImage();
        profileImage.setImageUrl(DEFAULT_AVATAR);
        profileImage.setUser(userEntity);
        userEntity.setProfileImage(profileImage);

        userRepository.save(userEntity);

        String otp = String.valueOf(new Random().nextInt(900000)+100000);

        VerificationToken token = new VerificationToken();
        token.setOtp(otp);
        token.setUser(userEntity);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        verificationTokenRepository.save(token);

        emailService.sendVerificationEmail(signupRequest.getEmail(), otp);
    }

    public boolean verifyUser(VerificationRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User with this email not found"));

        VerificationToken token = verificationTokenRepository.findByUser(user);

        if(token.getOtp().equals(request.getOtp()) && token.getExpiryDate().isAfter(LocalDateTime.now())) {
            user.setVerified(true);
            userRepository.save(user);

            verificationTokenRepository.delete(token);
            return true;
        }

        return false;
    }

    public void resendOtp(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if(user.isVerified()) {
            throw new RuntimeException("User is already verified");
        }

        String otp = String.valueOf(new Random().nextInt(900000)+100000);

        VerificationToken token = verificationTokenRepository.findByUser(user);
        if(token == null) {
            token = new VerificationToken();
            token.setUser(user);
        }

        token.setOtp(otp);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        verificationTokenRepository.save(token);

        emailService.sendVerificationEmail(email, otp);
    }

    public String loginUser(LoginRequest loginRequest) {
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
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with this email not found"));

        return user.isVerified();
    }
}
