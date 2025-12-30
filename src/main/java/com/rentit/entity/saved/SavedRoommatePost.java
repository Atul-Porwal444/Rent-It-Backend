package com.rentit.entity.saved;

import com.rentit.entity.post.RoommateListing;
import com.rentit.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Table(name = "saved_roommate_post", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "roommate_listing_id"})
})
@Entity
public class SavedRoommatePost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",  nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roommate_listing_id",  nullable = false)
    private RoommateListing  roommateListing;

    private LocalDateTime  savedOn;

    @PrePersist
    public void onCreate() {
        savedOn = LocalDateTime.now();
    }
}
