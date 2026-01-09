package com.rentit.service.postservice;

import com.rentit.entity.post.BaseListing;
import com.rentit.entity.post.RoomListing;
import com.rentit.entity.post.RoommateListing;
import com.rentit.entity.user.UserEntity;
import com.rentit.payload.request.post.BaseListingRequest;
import com.rentit.payload.request.post.RoomListingRequest;
import com.rentit.payload.request.post.RoommateListingRequest;
import com.rentit.repository.post.RoomListingRepository;
import com.rentit.repository.post.RoommateListingRepository;
import com.rentit.repository.user.UserRepository;
import com.rentit.service.media.ImageStorageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

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
                orElseThrow(() -> new UsernameNotFoundException("User not found"));
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
        listing.setDescription(request.getDescription());
        listing.setBhkType(request.getBhkType());
        listing.setFloorNumber(request.getFloorNumber());
        listing.setHasParking(request.isHasParking());
        listing.setFurnished(request.isFurnished());
        listing.setWaterSupply24x7(request.isWaterSupply24x7());
        listing.setElectricityBackup(request.isElectricityBackup());
        listing.setRentAmount(request.getRentAmount());
    }

}
