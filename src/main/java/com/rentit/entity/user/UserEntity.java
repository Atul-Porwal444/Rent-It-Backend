package com.rentit.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rentit.entity.post.RoomListing;
import com.rentit.entity.post.RoommateListing;
import com.rentit.entity.saved.SavedRoomPost;
import com.rentit.entity.saved.SavedRoommatePost;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfileEntity profile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProfileImage profileImage;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoomListing> roomListings = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RoommateListing> roommateListings = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<SavedRoomPost> savedRoomPostss = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade =  CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<SavedRoommatePost> savedRoommatePosts = new ArrayList<>();
}