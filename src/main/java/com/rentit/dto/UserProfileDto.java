package com.rentit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {
    private String name;
    private String email;
    private String phone;
    private String gender;
    private String dob;
    private String location;
    private String occupation;
    private String bio;
    private String profileUrl;
}
