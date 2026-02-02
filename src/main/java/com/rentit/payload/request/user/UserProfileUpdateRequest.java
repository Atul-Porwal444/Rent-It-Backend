package com.rentit.payload.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileUpdateRequest {
    private String location;
    private String dob;
    private String phoneNumber;
    private String gender;
    private String occupation;
    private String about;
}
