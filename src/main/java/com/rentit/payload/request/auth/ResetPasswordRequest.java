package com.rentit.payload.request.auth;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String email;
    private String otp;
    private String newPassword;
}
