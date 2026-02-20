package com.rentit.service.postservice;

import com.rentit.entity.post.BaseListing;
import com.rentit.entity.post.RoomListing;
import com.rentit.entity.post.RoommateListing;
import com.rentit.entity.user.UserEntity;
import com.rentit.exception.ResourceNotFoundException;
import com.rentit.payload.request.post.BaseListingRequest;
import com.rentit.payload.request.post.RoomListingRequest;
import com.rentit.payload.request.post.RoommateListingRequest;
import com.rentit.payload.response.PagedResponse;
import com.rentit.payload.response.post.BaseListingDto;
import com.rentit.payload.response.post.RoomListingDto;
import com.rentit.payload.response.post.RoommateListingDto;
import com.rentit.repository.post.RoomListingRepository;
import com.rentit.repository.post.RoommateListingRepository;
import com.rentit.repository.user.UserRepository;
import com.rentit.service.media.ImageStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListingService {

    private final UserRepository userRepository;

    private final ImageStorageService imageStorageService;

    private final RoomListingRepository roomListingRepository;

    private final RoommateListingRepository roommateListingRepository;

    private UserEntity getUserFromPrincipal(Principal principal) {
        String email = principal.getName();
        return userRepository.findByEmail(email).
                orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public void createRoomListing(Principal principal, RoomListingRequest request, List<MultipartFile> images) throws IOException {
        UserEntity userEntity = getUserFromPrincipal(principal);

        RoomListing room = new RoomListing();

        mapBaseFields(request, room);

        room.setSecurityDeposit(request.getSecurityDeposit());
        room.setAvailabilityStatus(request.getAvailabilityStatus());

        List<String> imageUrls = new ArrayList<>();
        for(MultipartFile file : images) {
            String pubicUrl = imageStorageService.uploadImage(file);
            imageUrls.add(pubicUrl);
        }

        room.setImageUrls(imageUrls);

        room.setUser(userEntity);
        roomListingRepository.save(room);
    }

    @Transactional
    public void createRoommateListing(Principal principal, RoommateListingRequest request, List<MultipartFile> images) throws IOException {
        UserEntity user = getUserFromPrincipal(principal);

        RoommateListing roommatePost = new RoommateListing();

        mapBaseFields(request, roommatePost);

        roommatePost.setLookingForGender(request.getLookingForGender());
        roommatePost.setReligionPreference(request.getReligionPreference());
        roommatePost.setDietaryPreference(request.getDietaryPreference());
        roommatePost.setCurrentRoommates(request.getCurrentRoommates());
        roommatePost.setNeededRoommates(request.getNeededRoommates());

        List<String> imageUrls = new ArrayList<>();
        for(MultipartFile file : images) {
            String publicUrl = imageStorageService.uploadImage(file);
            imageUrls.add(publicUrl);
        }

        roommatePost.setImageUrls(imageUrls);

        roommatePost.setUser(user);
        roommateListingRepository.save(roommatePost);
    }

    private void mapBaseFields(BaseListingRequest request, BaseListing listing) {
        listing.setLocation(request.getLocation());
        listing.setCity(request.getCity());
        listing.setState(request.getState());
        listing.setPincode(request.getPincode());
        listing.setDescription(request.getDescription());
        listing.setBhkType(request.getBhkType());
        listing.setFloorNumber(request.getFloorNumber());
        listing.setHasParking(request.isHasParking());
        listing.setFurnished(request.isFurnished());
        listing.setWaterSupply24x7(request.isWaterSupply24x7());
        listing.setElectricityBackup(request.isElectricityBackup());
        listing.setRentAmount(request.getRentAmount());
    }

    public PagedResponse<RoomListingDto> getAllRooms(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        // Create Pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        // Fetch paginated data from DB
        Page<RoomListing> roomPage = roomListingRepository.findAll(pageable);

        // Map Entities to DTOs
        List<RoomListingDto> content = roomPage.getContent().stream()
                .map(this::mapToRoomDto)
                .collect(Collectors.toList());

        // Return Standard Wrapper
        return new PagedResponse<>(
                content,
                roomPage.getNumber(),
                roomPage.getSize(),
                roomPage.getTotalElements(),
                roomPage.getTotalPages(),
                roomPage.isLast()
        );

    }

    public PagedResponse<RoommateListingDto> getAllRoommates(int pageNo, int pageSize, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        Page<RoommateListing> roommatePage = roommateListingRepository.findAll(pageable);

        List<RoommateListingDto> content = roommatePage.getContent().stream()
                .map(this::mapToRoommateDto)
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                roommatePage.getNumber(),
                roommatePage.getSize(),
                roommatePage.getTotalElements(),
                roommatePage.getTotalPages(),
                roommatePage.isLast()
        );
    }

    private RoomListingDto mapToRoomDto(RoomListing entity) {
        RoomListingDto dto = new RoomListingDto();

        // 1. Map all common Base properties
        mapBaseListingFields(entity, dto);

        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setUserName(entity.getUser().getName());

            if(entity.getUser().getProfileImage() != null) {
                dto.setUserProfileImageUrl(entity.getUser().getProfileImage().getImageUrl());
            }
        }

        // 2. Map Room specific properties
        dto.setSecurityDeposit(entity.getSecurityDeposit());
        dto.setAvailabilityStatus(entity.getAvailabilityStatus());

        return dto;
    }

    private RoommateListingDto mapToRoommateDto(RoommateListing entity) {
        RoommateListingDto dto = new RoommateListingDto();

        // 1. Map all common Base properties
        mapBaseListingFields(entity, dto);

        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setUserName(entity.getUser().getName());

            // If you have a profile image linked, map it. Otherwise, remove this line.
            if(entity.getUser().getProfileImage() != null) {
                dto.setUserProfileImageUrl(entity.getUser().getProfileImage().getImageUrl());
            }
        }

        // 2. Map Roommate specific properties
        dto.setLookingForGender(entity.getLookingForGender());
        dto.setReligionPreference(entity.getReligionPreference());
        dto.setDietaryPreference(entity.getDietaryPreference());
        dto.setCurrentRoommates(entity.getCurrentRoommates());
        dto.setNeededRoommates(entity.getNeededRoommates());

        return dto;
    }

    private void mapBaseListingFields(BaseListing entity, BaseListingDto dto) {
        dto.setId(entity.getId());
        dto.setImageUrls(entity.getImageUrls());
        dto.setLocation(entity.getLocation());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setPincode(entity.getPincode());
        dto.setDescription(entity.getDescription());
        dto.setBhkType(entity.getBhkType());
        dto.setFloorNumber(entity.getFloorNumber());
        dto.setHasParking(entity.isHasParking());
        dto.setFurnished(entity.isFurnished());
        dto.setWaterSupply24x7(entity.isWaterSupply24x7());
        dto.setElectricityBackup(entity.isElectricityBackup());
        dto.setRentAmount(entity.getRentAmount());
        dto.setPostedOn(entity.getPostedOn());
    }

}
