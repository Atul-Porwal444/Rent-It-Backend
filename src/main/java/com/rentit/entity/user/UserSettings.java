package com.rentit.entity.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_settings")
public class UserSettings {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
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
