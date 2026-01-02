package com.rentit.payload.request.post;

import lombok.Data;


@Data
public abstract class BaseListingRequest {

    private String location;
    private String city;
    private String description;
    private String bhkType;

    private int floorNumber;
    private boolean hasParking;
    private boolean isFurnished;
    private boolean waterSupply24x7;
    private boolean electricityBackup;

    private double rentAmount;
}
