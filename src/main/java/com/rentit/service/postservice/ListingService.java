package com.rentit.service.postservice;

import com.rentit.dto.ListingCardDto;
import com.rentit.entity.post.BaseListing;
import com.rentit.entity.post.RoomListing;
import com.rentit.entity.post.RoommateListing;
import com.rentit.entity.user.UserEntity;
import com.rentit.exception.BadRequestException;
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
import com.rentit.repository.saved.SavedRoomPostRepository;
import com.rentit.repository.saved.SavedRoommatePostRepository;
import com.rentit.repository.user.UserRepository;
import com.rentit.service.media.ImageStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ListingService {

    private final UserRepository userRepository;

    private final ImageStorageService imageStorageService;

    private final RoomListingRepository roomListingRepository;

    private final SavedRoomPostRepository savedRoomPostRepository;

    private final RoommateListingRepository roommateListingRepository;

    private final SavedRoommatePostRepository  savedRoommatePostRepository;

    private UserEntity getUserFromPrincipal(Principal principal) {
        if(principal == null) return null;
        log.info("DB call for fetching the user entity");
        return userRepository.findByEmail(principal.getName()).
                orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public void createRoomListing(Principal principal, RoomListingRequest request, List<MultipartFile> images) throws IOException {
        UserEntity userEntity = getUserFromPrincipal(principal);

        RoomListing room = new RoomListing();

        mapBaseFields(request, room);

        room.setSecurityDeposit(request.getSecurityDeposit());

        List<String> imageUrls = new ArrayList<>();
        for(MultipartFile file : images) {
            String pubicUrl = imageStorageService.uploadImage(file);
            imageUrls.add(pubicUrl);
        }

        room.setImageUrls(imageUrls);

        room.setUser(userEntity);
        log.info("DB call for creating the room post");
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
        log.info("DB call for creating the roommate post");
        roommateListingRepository.save(roommatePost);
    }

    private void mapBaseFields(BaseListingRequest request, BaseListing listing) {
        listing.setLocation(request.getLocation());
        listing.setCity(request.getCity());
        listing.setState(request.getState());
        listing.setPincode(request.getPincode());
        listing.setDescription(request.getDescription());
        listing.setBhkType(request.getBhkType());
        listing.setAvailabilityStatus(true);
        listing.setFloorNumber(request.getFloorNumber());
        listing.setHasParking(request.isHasParking());
        listing.setFurnished(request.isFurnished());
        listing.setWaterSupply24x7(request.isWaterSupply24x7());
        listing.setElectricityBackup(request.isElectricityBackup());
        listing.setRentAmount(request.getRentAmount());
    }

    public RoomListingDto getRoomById(Long id, Principal principal) {
        log.info("DB call for getting the single room post by id");
        RoomListing room = roomListingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        UserEntity currentUser = getUserFromPrincipal(principal);
        return mapToRoomDto(room, currentUser, true);
    }

    public RoommateListingDto getRoommateById(Long id, Principal principal) {
        log.info("DB call for getting the single roommate post by id");
        RoommateListing roommate = roommateListingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Roommate not found"));

        UserEntity currentUser = getUserFromPrincipal(principal);
        return mapToRoommateDto(roommate, currentUser, true);
    }

    public PagedResponse<RoomListingDto> getAllRooms(
            String query, String bhk, Double min, Double max,
            boolean furnish, boolean park, boolean water, boolean elec,int pageNo, int pageSize, String sortBy, String sortDir) {

        Sort sort;
        if ("rentAmountAsc".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.ASC, "rentAmount");
        } else if ("rentAmountDesc".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "rentAmount");
        } else {
            sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                    ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();
        }

        // Create Pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        // Fetch paginated data from DB
        log.info("DB call for getting the room post by applying the filter");
        Page<RoomListing> roomPage = roomListingRepository.findFilteredRooms(
                query, bhk, min, max, furnish, park, water, elec,pageable);

        // Map Entities to DTOs
        List<RoomListingDto> content = roomPage.getContent().stream()
                .map((RoomListing entity) -> mapToRoomDto(entity, null, false))
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

    public PagedResponse<RoommateListingDto> getAllRoommates(
            String query, String bhk , String gender, String diet, String religion,Double min, Double max,
            boolean furnish, boolean park, boolean water, boolean elec,int pageNo, int pageSize, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        log.info("DB call for getting the roommate post by applying the filter");
        Page<RoommateListing> roommatePage = roommateListingRepository.findFilteredRoommates(
                query, bhk, gender, diet, religion, min, max, furnish, park, water, elec,pageable);

        List<RoommateListingDto> content = roommatePage.getContent().stream()
                .map((RoommateListing entity) -> mapToRoommateDto(entity, null, false))
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

    public List<RoomListingDto> getMyRooms(Principal principal) {
        UserEntity currentUser = getUserFromPrincipal(principal);

        log.info("DB call for fetching the room post of particular user");
        return roomListingRepository.findByUser(currentUser).stream()
                .map((RoomListing entity) -> mapToRoomDto(entity, currentUser, false)).collect(Collectors.toList());
    }

    public List<RoommateListingDto> getMyRoommates(Principal principal) {
        UserEntity currentUser = getUserFromPrincipal(principal);

        log.info("DB call for fetching the roommate post of particular user");
        return roommateListingRepository.findByUser(currentUser).stream()
                .map((RoommateListing entity) -> mapToRoommateDto(entity, currentUser, false)).collect(Collectors.toList());
    }

    public boolean updateRoomStatus(Long id, Principal principal) {
        UserEntity currentUser = getUserFromPrincipal(principal);

        RoomListing roomListing = roomListingRepository.readById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room post not found for this ID"));

        UserEntity owner = roomListing.getUser();

        if(owner != null && Objects.equals(owner.getId(), currentUser.getId())) {
            if(roomListing.isAvailabilityStatus()) {
                roomListing.setAvailabilityStatus(false);
                roomListingRepository.save(roomListing);
            }
            else {
                roomListing.setAvailabilityStatus(true);
                roomListingRepository.save(roomListing);
            }
        }
        else {
            throw new BadRequestException("You are not the owner of the room post");
        }
        return true;
    }

    public boolean updateRoommateStatus(Long id, Principal principal) {
        UserEntity currentUser = getUserFromPrincipal(principal);

        RoommateListing roommateListing = roommateListingRepository.readById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Roommate post not found for this ID"));

        UserEntity owner = roommateListing.getUser();

        if(owner != null && Objects.equals(owner.getId(), currentUser.getId())) {
            if(roommateListing.isAvailabilityStatus()) {
                roommateListing.setAvailabilityStatus(false);
                roommateListingRepository.save(roommateListing);
            }
            else {
                roommateListing.setAvailabilityStatus(true);
                roommateListingRepository.save(roommateListing);
            }
        }
        else {
            throw new BadRequestException("You are not the owner of the roommate post");
        }
        return true;
    }

    public List<ListingCardDto> getRoomCards(String targetCity, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("postedOn").descending());
        return roomListingRepository.findCardByCity(targetCity, pageable)
                .stream().map(this::mapToRoomCardDto).collect(Collectors.toList());
    }

    public List<ListingCardDto> getRoommateCards(String targetCity, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("postedOn").descending());
        return roommateListingRepository.findCardByCity(targetCity, pageable)
                .stream().map(this::mapToRoommateCardDto).collect(Collectors.toList());
    }

    public void deleteRoom(Long id, Principal principal) {
        log.info("DB call for fetching the room post by id");
        RoomListing room = roomListingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        log.info("DB call for getting the user from the room post entity");
        if(!room.getUser().getEmail().equals(principal.getName())) {
            throw new RuntimeException("Unauthorized to delete this post");
        }

        log.info("DB call for deleting the room post");
        roomListingRepository.delete(room);
    }

    public void deleteRoommate(Long id, Principal principal) {
        log.info("DB call for fetching the roommate post by id");
        RoommateListing roommate = roommateListingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Roommate not found"));

        log.info("DB call for getting the user from the roommate post entity");
        if(!roommate.getUser().getEmail().equals(principal.getName())) {
            throw new RuntimeException("Unauthorized to delete this post");
        }

        log.info("DB call for deleting the room post");
        roommateListingRepository.delete(roommate);
    }

    public RoomListingDto mapToRoomDto(RoomListing entity, UserEntity currentUser, boolean fetchSave) {
        RoomListingDto dto = new RoomListingDto();
        mapBaseListingFields(entity, dto);

        if (currentUser != null && fetchSave) {
            dto.setSavedByUser(savedRoomPostRepository.findByUserAndRoomListing(currentUser, entity).isPresent());
        }

        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setUserName(entity.getUser().getName());
            dto.setUserProfileImageUrl(entity.getUser().getProfileImageUrl());

            if (entity.getUser().getUserSettings() != null) {
                dto.setShowEmail(entity.getUser().getUserSettings().isShowEmail());
                dto.setShowPhone(entity.getUser().getUserSettings().isShowPhone());
                if (dto.isShowEmail()) dto.setUserEmail(entity.getUser().getEmail());
                if (dto.isShowPhone()) dto.setUserPhone(entity.getUser().getProfile().getPhone());
            }
        }

        dto.setSecurityDeposit(entity.getSecurityDeposit());
        return dto;
    }

    public RoommateListingDto mapToRoommateDto(RoommateListing entity, UserEntity currentUser, boolean fetchSave) {
        RoommateListingDto dto = new RoommateListingDto();
        mapBaseListingFields(entity, dto);

        if (currentUser != null && fetchSave) {
            dto.setSavedByUser(savedRoommatePostRepository.findByUserAndRoommateListing(currentUser, entity).isPresent());
        }

        if (entity.getUser() != null) {
            dto.setUserId(entity.getUser().getId());
            dto.setUserName(entity.getUser().getName());
            dto.setUserProfileImageUrl(entity.getUser().getProfileImageUrl());

            if (entity.getUser().getUserSettings() != null) {
                dto.setShowEmail(entity.getUser().getUserSettings().isShowEmail());
                dto.setShowPhone(entity.getUser().getUserSettings().isShowPhone());
                if (dto.isShowEmail()) dto.setUserEmail(entity.getUser().getEmail());
                if (dto.isShowPhone()) dto.setUserPhone(entity.getUser().getProfile().getPhone());
            }
        }

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
        dto.setAvailabilityStatus(entity.isAvailabilityStatus());
        dto.setHasParking(entity.isHasParking());
        dto.setFurnished(entity.isFurnished());
        dto.setWaterSupply24x7(entity.isWaterSupply24x7());
        dto.setElectricityBackup(entity.isElectricityBackup());
        dto.setRentAmount(entity.getRentAmount());
        dto.setPostedOn(entity.getPostedOn());
    }

    public ListingCardDto mapToRoomCardDto(RoomListing entity) {
        ListingCardDto dto = new ListingCardDto();
        dto.setId(entity.getId());
        dto.setRentAmount(entity.getRentAmount());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setPincode(entity.getPincode());
        if(entity.getImageUrls() != null) {
            dto.setImageUrls(entity.getImageUrls().getFirst());
        }
        return dto;
    }

    public ListingCardDto mapToRoommateCardDto(RoommateListing entity) {
        ListingCardDto dto = new ListingCardDto();
        dto.setId(entity.getId());
        dto.setRentAmount(entity.getRentAmount());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setPincode(entity.getPincode());
        if(entity.getImageUrls() != null) {
            dto.setImageUrls(entity.getImageUrls().getFirst());
        }
        return dto;
    }


}
