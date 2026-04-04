package com.rentit.repository.user;

import com.rentit.entity.user.UserEntity;
import com.rentit.repository.UserAuthProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {

    Optional<UserAuthProjection> getByEmail(String email);

    Optional<UserEntity> findByEmail(String email);

}
