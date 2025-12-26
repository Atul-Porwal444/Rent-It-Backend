package com.rentit.payload.request.post;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class RoomListingRequest extends BaseListingRequest{
    private double securityDeposit;
    private String availabilityStatus;
}
