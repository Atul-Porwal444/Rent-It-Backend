package com.rentit.repository.saved;

import com.rentit.entity.post.RoommateListing;
import com.rentit.entity.saved.SavedRoommatePost;
import com.rentit.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedRoommatePostRepository extends JpaRepository<SavedRoommatePost, Long> {

    List<SavedRoommatePost> findByUser(UserEntity userEntity);

    Optional<SavedRoommatePost> findByUserAndRoommateListing(UserEntity userEntity, RoommateListing roommateListing);
}
