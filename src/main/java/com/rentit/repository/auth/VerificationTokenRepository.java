package com.rentit.repository.auth;

import com.rentit.entity.auth.VerificationToken;
import com.rentit.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByUser(UserEntity user);
}
