package com.rentit.repository.post;


import com.rentit.entity.post.RoommateListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoommateListingRepository extends JpaRepository<RoommateListing, Long> {

    // Find roommates in a specific city
    List<RoommateListing> findByCity(String city);

    // Find posts looking for a specific gender
    List<RoommateListing> findByLookingForGender(String gender);

    // Find by City and specific gender requirement
    List<RoommateListing> findByCityAndLookingForGender(String city, String gender);

    // Find the post according to the relevant diet
    List<RoommateListing> findByDietaryPreference(String preference);
}