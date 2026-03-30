package com.rentit.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rentit.entity.auth.VerificationToken;
import com.rentit.entity.post.RoomListing;
import com.rentit.entity.post.RoommateListing;
import com.rentit.entity.saved.SavedRoomPost;
import com.rentit.entity.saved.SavedRoommatePost;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "target_city")
    private String targetCity;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private UserProfileEntity profile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
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

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private VerificationToken verificationToken;

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Notification> notification;

    @OneToOne(mappedBy = "user", orphanRemoval = true,  cascade = CascadeType.ALL)
    private UserSettings userSettings;
}