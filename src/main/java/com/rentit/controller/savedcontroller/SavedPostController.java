package com.rentit.controller.savedcontroller;

import com.rentit.payload.response.ApiResponse;
import com.rentit.service.savedpostservice.SavedPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/save")
public class SavedPostController {

    private final SavedPostService savedPostService;

    @PostMapping("/room")
    public ResponseEntity<ApiResponse> toggleSavedRoom(Principal principal, @RequestParam Long postId) {
        try {

            savedPostService.toggleSavedRoom(principal, postId);

            return ResponseEntity.ok(new ApiResponse(true, "Toggled room post Successfully", null));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Internal Server Error: " + e.getMessage(), null));
        }
    }

    @PostMapping("/roommate")
    public ResponseEntity<ApiResponse> toggleSavedRoommate(Principal principal, @RequestParam Long postId) {
        try {

            savedPostService.toggleSavedRoommate(principal, postId);

            return ResponseEntity.ok(new ApiResponse(true, "Toggled roommate post Successfully", null));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Internal Server Error: " + e.getMessage(), null));
        }
    }

}
