package com.rentit.entity.post;

import com.rentit.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "room_listings")
@Data
@EqualsAndHashCode(callSuper = true)
public class RoomListing extends BaseListing {

    private double securityDeposit;
    private String availabilityStatus; // "Available", "Booked"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
