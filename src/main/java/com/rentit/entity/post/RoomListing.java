package com.rentit.entity.post;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rentit.entity.saved.SavedRoomPost;
import com.rentit.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "room_listings")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class RoomListing extends BaseListing {

    private double securityDeposit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "roomListing", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<SavedRoomPost> savedByUsers = new ArrayList<>();


}
