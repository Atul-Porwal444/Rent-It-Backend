package com.rentit.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class HListingCardDto {
    private Long id;
    private boolean availabilityStatus;
    private LocalDate postedOn;
    private double rentAmount;
    private String location;
    private String city;
    private String state;
    private String pincode;
    private boolean hasParking;
    private boolean isFurnished;

    private String lookingForGender;
    private String dietaryPreference;
    private int currentRoommates;

    private List<String> imageUrls;

}
