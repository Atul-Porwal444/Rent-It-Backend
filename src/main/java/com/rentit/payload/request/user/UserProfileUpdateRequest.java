package com.rentit.payload.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileUpdateRequest {
    private String name;
    private String location;
    private String dob;
    private String phone;
    private String gender;
    private String occupation;
    private String bio;
}
