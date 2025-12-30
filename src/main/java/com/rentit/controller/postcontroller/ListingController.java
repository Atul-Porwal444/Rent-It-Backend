package com.rentit.controller.postcontroller;

import com.rentit.entity.user.UserEntity;
import com.rentit.payload.request.post.RoomListingRequest;
import com.rentit.payload.request.post.RoommateListingRequest;
import com.rentit.payload.response.ApiResponse;
import com.rentit.repository.user.UserRepository;
import com.rentit.service.postservice.ListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/list")
public class ListingController {

    private final ObjectMapper objectMapper;

    private final UserRepository userRepository;

    private final ListingService listingService;


    @PostMapping(value = "/room", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> createRoomListing(@RequestPart("data") String jsonListing, @RequestPart("images")List<MultipartFile> files, Principal principal) throws IOException {
        try {

            RoomListingRequest  roomListingRequest = objectMapper.readValue(jsonListing, RoomListingRequest.class);

            listingService.createRoomListing(principal, roomListingRequest, files);

            return ResponseEntity.ok(new ApiResponse(true, "Post successfully",  roomListingRequest));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Post unsuccessful" + e.getMessage(), null));
        }
    }

    @PostMapping(value = "/roommate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> createRoommateListing(@RequestPart("data") String jsonListing, @RequestPart("images")List<MultipartFile> files, Principal principal) throws IOException {
        try {

            RoommateListingRequest roommateListingRequest = objectMapper.readValue(jsonListing, RoommateListingRequest.class);

            listingService.createRoommateListing(principal, roommateListingRequest, files);

            return ResponseEntity.ok(new ApiResponse(true, "Post successfully",  roommateListingRequest));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Post unsuccessful" + e.getMessage(), null));
        }
    }

}
