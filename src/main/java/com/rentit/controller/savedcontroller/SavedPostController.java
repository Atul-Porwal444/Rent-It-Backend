package com.rentit.controller.savedcontroller;

import com.rentit.payload.response.ApiResponse;
import com.rentit.service.savedpostservice.SavedPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/save")
public class SavedPostController {

    private final SavedPostService savedPostService;

    @PostMapping("/room/{postId}")
    public ResponseEntity<ApiResponse> toggleSavedRoom(Principal principal, @PathVariable Long postId) {
        try {

            savedPostService.toggleSavedRoom(principal, postId);

            return ResponseEntity.ok(new ApiResponse(true, "Toggled room post Successfully", null));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Internal Server Error: " + e.getMessage(), null));
        }
    }

    @PostMapping("/roommate/{postId}")
    public ResponseEntity<ApiResponse> toggleSavedRoommate(Principal principal, @PathVariable Long postId) {
        try {

            savedPostService.toggleSavedRoommate(principal, postId);

            return ResponseEntity.ok(new ApiResponse(true, "Toggled roommate post Successfully", null));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Internal Server Error: " + e.getMessage(), null));
        }
    }

    @GetMapping("/room/{postId}/status")
    public ResponseEntity<Boolean> checkRoomStatus(Principal principal, @PathVariable Long postId) {
        return ResponseEntity.ok(savedPostService.isRoomSaved(principal, postId));
    }

    @GetMapping("roommate/{postId}/status")
    public ResponseEntity<Boolean> checkRoommateStatus(Principal principal, @PathVariable Long postId) {
        return ResponseEntity.ok(savedPostService.isRoommateSaved(principal, postId));
    }

}
