package com.rentit.payload.request.post;

import lombok.Data;

import java.util.List;


@Data
public abstract class BaseListingRequest {

    private String location;
    private String city;
    private String description;
    private String bhkType;

    List<String> imageUrls;

    private int floorNumber;
    private boolean hasParking;
    private boolean isFurnished;
    private boolean waterSupply24x7;
    private boolean electricityBackup;

    private double rentAmount;
}
