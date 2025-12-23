package com.rentit.service.authservice;

import com.rentit.entity.UserEntity;
import com.rentit.payload.request.LoginRequest;
import com.rentit.payload.request.SignupRequest;
import com.rentit.repository.UserRepository;
import com.rentit.utility.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public void registerUser(SignupRequest signupRequest) {
        if(userRepository.findByEmail(signupRequest.getEmail()).isPresent()){
            throw new RuntimeException("Email Already Exists");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setName(signupRequest.getName());
        userEntity.setEmail(signupRequest.getEmail());
        userEntity.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        userRepository.save(userEntity);
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
}
