package com.rentit.dto;

import lombok.Data;

@Data
public class ListingCardDto {
    private Long id;
    private double rentAmount;
    private String city;
    private String state;
    private String pincode;
    private String imageUrls;
}
