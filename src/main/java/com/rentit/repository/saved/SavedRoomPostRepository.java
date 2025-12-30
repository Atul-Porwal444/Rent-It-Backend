package com.rentit.repository.saved;


import com.rentit.entity.post.RoomListing;
import com.rentit.entity.saved.SavedRoomPost;
import com.rentit.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedRoomPostRepository extends JpaRepository<SavedRoomPost, Long> {

    List<SavedRoomPost> findByUser(UserEntity user);

    Optional<SavedRoomPost> findByUserAndRoomListing(UserEntity userEntity, RoomListing roomListing);

}
