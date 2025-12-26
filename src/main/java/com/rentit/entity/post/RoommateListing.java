package com.rentit.entity.post;

import com.rentit.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "roommate_listings")
@Data
@EqualsAndHashCode(callSuper = true)
public class RoommateListing extends BaseListing {

    private String lookingForGender;
    private String religionPreference;
    private String dietaryPreference;

    private int currentRoommates;
    private int neededRoommates;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
