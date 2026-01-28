package com.rentit.repository.auth;

import com.rentit.entity.auth.VerificationToken;
import com.rentit.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByUser(UserEntity user);
}
