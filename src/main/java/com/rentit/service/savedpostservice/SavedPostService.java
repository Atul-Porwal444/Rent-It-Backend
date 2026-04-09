package com.rentit.service.savedpostservice;

import com.rentit.dto.ListingCardDto;
import com.rentit.entity.post.RoomListing;
import com.rentit.entity.post.RoommateListing;
import com.rentit.entity.saved.SavedRoomPost;
import com.rentit.entity.saved.SavedRoommatePost;
import com.rentit.entity.user.UserEntity;
import com.rentit.exception.ResourceNotFoundException;
import com.rentit.payload.response.post.RoomListingDto;
import com.rentit.payload.response.post.RoommateListingDto;
import com.rentit.repository.post.RoomListingRepository;
import com.rentit.repository.post.RoommateListingRepository;
import com.rentit.repository.saved.SavedRoomPostRepository;
import com.rentit.repository.saved.SavedRoommatePostRepository;
import com.rentit.repository.user.UserRepository;
import com.rentit.service.postservice.ListingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SavedPostService {

    private final UserRepository userRepository;

    private final ListingService listingService;

    private final RoomListingRepository roomListingRepository;

    private final RoommateListingRepository roommateListingRepository;

    private final SavedRoomPostRepository  savedRoomPostRepository;

    private final SavedRoommatePostRepository savedRoommatePostRepository;

    private UserEntity getUserFromPrincipal(Principal principal) {
        log.info("DB call for fetching the user");
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Username not found"));
    }

    public boolean isRoomSaved(Principal principal, Long postId) {
        UserEntity user = getUserFromPrincipal(principal);

        log.info("DB call for fetching the room by id");
        RoomListing room = roomListingRepository.readById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Room listing not found"));

        log.info("DB call for checking the room post saved the user or not");
        return savedRoomPostRepository.findByUserAndRoomListing(user, room).isPresent();
    }

    public boolean isRoommateSaved(Principal principal, Long postId) {
        UserEntity user = getUserFromPrincipal(principal);

        log.info("DB call for fetching the roommate by id");
        RoommateListing roommate = roommateListingRepository.readById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Roommate listing not found"));

        log.info("DB call for checking the roommate post saved the user or not");
        return savedRoommatePostRepository.findByUserAndRoommateListing(user, roommate).isPresent();
    }

    public void toggleSavedRoom(Principal principal, Long postId) {
        UserEntity user = getUserFromPrincipal(principal);

        log.info("DB call for fetching the room by id");
        RoomListing room = roomListingRepository.readById(postId).orElseThrow(() ->
                new ResourceNotFoundException("Room post Not Found"));

        log.info("DB call for fetching the room post saved by user or not");
        Optional<SavedRoomPost> existing = savedRoomPostRepository.findByUserAndRoomListing(user,room);

        if(existing.isPresent()) {
            log.info("DB call for deleting the saved room post");
            savedRoomPostRepository.delete(existing.get());
        }
        else  {
            SavedRoomPost savedRoomPost = new SavedRoomPost();
            savedRoomPost.setUser(user);
            savedRoomPost.setRoomListing(room);
            log.info("DB call for saving the room post");
            savedRoomPostRepository.save(savedRoomPost);
        }
    }

    public void toggleSavedRoommate(Principal principal, Long postId) {
        UserEntity user = getUserFromPrincipal(principal);

        log.info("DB call for fetching the roommate by id");
        RoommateListing roommate = roommateListingRepository.readById(postId).orElseThrow(() ->
                new ResourceNotFoundException("Roommate post Not Found"));

        log.info("DB call for fetching the roommate post saved by user or not");
        Optional<SavedRoommatePost> existing = savedRoommatePostRepository.findByUserAndRoommateListing(user,roommate);

        if(existing.isPresent()) {
            log.info("DB call for deleting the saved roommate post");
            savedRoommatePostRepository.delete(existing.get());
        }
        else {
            SavedRoommatePost savedRoommatePost = new SavedRoommatePost();
            savedRoommatePost.setUser(user);
            savedRoommatePost.setRoommateListing(roommate);
            log.info("DB call for saving the roommate post");
            savedRoommatePostRepository.save(savedRoommatePost);
        }
    }

    public List<ListingCardDto> getSavedRoomsForUser(Principal principal) {
        UserEntity user = getUserFromPrincipal(principal);

        log.info("DB call for fetching the saved rooms by user");
        return savedRoomPostRepository.findByUser(user).stream()
                .map(saved -> listingService.mapToRoomCardDto(saved.getRoomListing()))
                .collect(Collectors.toList());
    }

    public List<ListingCardDto> getSavedRoommatesForUser(Principal principal) {
        UserEntity user = getUserFromPrincipal(principal);

        log.info("DB call for fetching the saved roommates by user");
        return savedRoommatePostRepository.findByUser(user).stream()
                .map(saved -> listingService.mapToRoommateCardDto(saved.getRoommateListing()))
                .collect(Collectors.toList());
    }
}
