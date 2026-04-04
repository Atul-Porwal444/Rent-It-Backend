package com.rentit.repository;

public interface UserAuthProjection {
    String getEmail();
    String getPassword();
    boolean getIsVerified();
}
