package com.rentit.payload.response.post;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RoommateListingDto extends BaseListingDto{

    private String lookingForGender;
    private String religionPreference;
    private String dietaryPreference;

    private int currentRoommates;
    private int neededRoommates;
}
