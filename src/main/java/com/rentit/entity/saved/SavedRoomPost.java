package com.rentit.entity.saved;

import com.rentit.entity.post.RoomListing;
import com.rentit.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table(name = "saved_room_post", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "room_listing_id"})
})
@Entity
public class SavedRoomPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_listing_id", nullable = false)
    private RoomListing roomListing;

    private LocalDateTime savedOn;

    @PrePersist
    public void onCreate() {
        savedOn = LocalDateTime.now();
    }
}
