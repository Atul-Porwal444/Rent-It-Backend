package com.rentit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// This call is useful when the user hit and api that is authenticated and there is no token found
// Instead of send just 403 we will configure it

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String jsonResponse = "{\"success\":false,\"message\": \"Access Denied: Missing or Invalid JWT Token.\"}";
        response.getWriter().write(jsonResponse);
    }
}
