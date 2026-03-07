package com.rentit.dto;

import lombok.Data;

@Data
public class UserSettingsDto {
    private boolean showEmail;
    private boolean showPhone;
    private boolean allowMessages;
    private boolean emailAlerts;
    private boolean newRoomMatches;
    private boolean promotionalOffers;
}
