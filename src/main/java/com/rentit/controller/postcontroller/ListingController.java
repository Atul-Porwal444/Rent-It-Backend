package com.rentit.controller.postcontroller;

import com.rentit.exception.BadRequestException;
import com.rentit.exception.ResourceNotFoundException;
import com.rentit.payload.request.post.RoomListingRequest;
import com.rentit.payload.request.post.RoommateListingRequest;
import com.rentit.payload.response.ApiResponse;
import com.rentit.payload.response.PagedResponse;
import com.rentit.payload.response.post.RoomListingDto;
import com.rentit.payload.response.post.RoommateListingDto;
import com.rentit.repository.user.UserRepository;
import com.rentit.service.postservice.ListingService;
import com.rentit.utility.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.security.Principal;
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

            RoomListingRequest roomListingRequest = objectMapper.readValue(jsonListing, RoomListingRequest.class);

            listingService.createRoomListing(principal, roomListingRequest, files);

            return ResponseEntity.ok(new ApiResponse(true, "Post successfully",  roomListingRequest));

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Post unsuccessful" + e.getMessage(), null));
        }
    }

    @PostMapping(value = "/roommate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> createRoommateListing(@RequestPart("data") String jsonListing, @RequestPart("images")List<MultipartFile> files, Principal principal) throws IOException {
        try {

            RoommateListingRequest roommateListingRequest = objectMapper.readValue(jsonListing, RoommateListingRequest.class);

            listingService.createRoommateListing(principal, roommateListingRequest, files);

            return ResponseEntity.ok(new ApiResponse(true, "Post successfully",  roommateListingRequest));

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Post unsuccessful" + e.getMessage(), null));
        }
    }

    @GetMapping("/rooms/{id}")
    public ResponseEntity<RoomListingDto> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(listingService.getRoomById(id));
    }

    @GetMapping("/rooms")
    public ResponseEntity<PagedResponse<RoomListingDto>> getRooms(
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) String bhkType,
            @RequestParam(required = false) Double minRent,
            @RequestParam(required = false) Double maxRent,
            @RequestParam(defaultValue = "false") boolean isFurnished,
            @RequestParam(defaultValue = "false") boolean hasParking,
            @RequestParam(defaultValue = "false") boolean waterSupply24x7,
            @RequestParam(defaultValue = "false") boolean electricityBackup,
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ) {
        // Validation constraint check
        if (pageNo < 0) throw new BadRequestException("Page index must not be less than zero");

        return ResponseEntity.ok(listingService.getAllRooms(searchQuery, bhkType, minRent, maxRent,
                isFurnished, hasParking, waterSupply24x7, electricityBackup,pageNo, pageSize, sortBy, sortDir));
    }

    @GetMapping("/roommates")
    public ResponseEntity<PagedResponse<RoommateListingDto>> getRoommates(
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) String bhkType,
            @RequestParam(required = false) String lookingForGender,
            @RequestParam(required = false) String dietaryPreference,
            @RequestParam(required = false) String religionPreference,
            @RequestParam(required = false) Double minRent,
            @RequestParam(required = false) Double maxRent,
            @RequestParam(defaultValue = "false") boolean isFurnished,
            @RequestParam(defaultValue = "false") boolean hasParking,
            @RequestParam(defaultValue = "false") boolean waterSupply24x7,
            @RequestParam(defaultValue = "false") boolean electricityBackup,
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ) {
        if (pageNo < 0) throw new BadRequestException("Page index must not be less than zero");

        return ResponseEntity.ok(listingService.getAllRoommates(searchQuery, bhkType ,lookingForGender, dietaryPreference, religionPreference,
                minRent, maxRent, isFurnished, hasParking, waterSupply24x7, electricityBackup,pageNo, pageSize, sortBy, sortDir));
    }

}
