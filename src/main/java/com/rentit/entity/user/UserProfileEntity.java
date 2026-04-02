package com.rentit.entity.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileEntity {

    @Id
    private Long id;

    private String location;

    private String dob;

    private String phone;

    private String gender;

    private String occupation;

    private String bio;


    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
