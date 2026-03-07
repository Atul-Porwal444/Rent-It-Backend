package com.rentit.payload.request.auth;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    private String email;
}
