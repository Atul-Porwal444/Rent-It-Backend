package com.rentit.entity.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rentit.entity.saved.SavedRoomPost;
import com.rentit.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "roomListing", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<SavedRoomPost> savedByUsers = new ArrayList<>();


}
