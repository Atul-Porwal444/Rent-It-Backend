package com.rentit.payload.response.post;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public abstract class BaseListingDto {
    private Long id;

    private String location;
    private String city;
    private String state;
    private String pincode;

    private String description;
    private String bhkType;
    private int floorNumber;
    private double rentAmount;

    private boolean hasParking;
    private boolean isFurnished;
    private boolean waterSupply24x7;
    private boolean electricityBackup;

    private List<String> imageUrls;
    private LocalDate postedOn;


    private Long userId;
    private String userName;
    private String userProfileImageUrl;
}
