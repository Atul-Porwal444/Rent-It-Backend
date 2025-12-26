package com.rentit.payload.request.post;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RoommateListingRequest extends BaseListingRequest {

    private String lookingForGender;
    private String religionPreference;
    private String dietaryPreference;
    private int currentRoommates;
    private int neededRoommates;
}