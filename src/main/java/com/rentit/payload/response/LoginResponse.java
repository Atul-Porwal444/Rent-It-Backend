package com.rentit.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private Long id;
    private String token;
    private String name;
    private String email;
    private String profileUrl;
}
