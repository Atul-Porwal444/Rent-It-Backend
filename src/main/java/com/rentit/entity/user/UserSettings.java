package com.rentit.entity.user;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user_settings")
public class UserSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    //Privacy Settings
    private boolean showEmail = true;
    private boolean showPhone = false;
    private boolean allowMessages = true;

    //Notification Preference
    private boolean emailAlerts = true;
    private boolean newRoomMatches = true;
    private boolean promotionalOffers = false;

}
