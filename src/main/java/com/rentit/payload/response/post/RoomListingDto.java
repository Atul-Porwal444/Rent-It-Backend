package com.rentit.payload.response.post;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RoomListingDto extends BaseListingDto{

    private double securityDeposit;
    private String availabilityStatus;
}
