package com.rentit.service.savedpostservice;

import com.rentit.entity.post.RoomListing;
import com.rentit.entity.post.RoommateListing;
import com.rentit.entity.saved.SavedRoomPost;
import com.rentit.entity.saved.SavedRoommatePost;
import com.rentit.entity.user.UserEntity;
import com.rentit.repository.post.RoomListingRepository;
import com.rentit.repository.post.RoommateListingRepository;
import com.rentit.repository.saved.SavedRoomPostRepository;
import com.rentit.repository.saved.SavedRoommatePostRepository;
import com.rentit.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SavedPostService {

    private final UserRepository userRepository;

    private final RoomListingRepository roomListingRepository;

    private final RoommateListingRepository roommateListingRepository;

    private final SavedRoomPostRepository  savedRoomPostRepository;

    private final SavedRoommatePostRepository savedRoommatePostRepository;

    private UserEntity getUserFromPrincipal(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }

    public void toggleSavedRoom(Principal principal, Long postId) {
        UserEntity user = getUserFromPrincipal(principal);
        RoomListing room = roomListingRepository.findById(postId).orElseThrow(() ->
                new RuntimeException("Room post Not Found"));

        Optional<SavedRoomPost> existing = savedRoomPostRepository.findByUserAndRoomListing(user,room);

        if(existing.isPresent()) {
            System.out.println("Removing from saved");
            savedRoomPostRepository.delete(existing.get());
        }
        else  {
            System.out.println("Adding to saved");
            SavedRoomPost savedRoomPost = new SavedRoomPost();
            savedRoomPost.setUser(user);
            savedRoomPost.setRoomListing(room);
            savedRoomPostRepository.save(savedRoomPost);
        }
    }

    public void toggleSavedRoommate(Principal principal, Long postId) {
        UserEntity user = getUserFromPrincipal(principal);
        RoommateListing roommate = roommateListingRepository.findById(postId).orElseThrow(() ->
                new RuntimeException("Roommate post Not Found"));

        Optional<SavedRoommatePost> existing = savedRoommatePostRepository.findByUserAndRoommateListing(user,roommate);

        if(existing.isPresent()) {
            System.out.println("Removing from saved");
            savedRoommatePostRepository.delete(existing.get());
        }
        else {
            System.out.println("Adding to saved");
            SavedRoommatePost savedRoommatePost = new SavedRoommatePost();
            savedRoommatePost.setUser(user);
            savedRoommatePost.setRoommateListing(roommate);
            savedRoommatePostRepository.save(savedRoommatePost);
        }
    }


}
