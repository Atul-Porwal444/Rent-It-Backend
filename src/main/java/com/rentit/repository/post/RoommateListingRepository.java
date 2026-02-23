package com.rentit.repository.post;


import com.rentit.entity.post.RoommateListing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT r FROM RoommateListing r WHERE " +
            "(:query IS NULL OR :query = '' OR LOWER(r.city) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(r.location) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND (:bhk IS NULL OR :bhk = '' OR r.bhkType = :bhk) " +
            "AND (:gender IS NULL OR :gender = '' OR r.lookingForGender = :gender) " +
            "AND (:diet IS NULL OR :diet = '' OR r.dietaryPreference = :diet) " +
            "AND (:religion IS NULL OR :religion = '' OR r.religionPreference = :religion) " +
            "AND (:min IS NULL OR r.rentAmount >= :min) " +
            "AND (:max IS NULL OR r.rentAmount <= :max) " +
            "AND (:furnish = false OR r.isFurnished = true) " +
            "AND (:park = false OR r.hasParking = true) " +
            "AND (:water = false OR r.waterSupply24x7 = true) " +
            "AND (:elec = false OR r.electricityBackup = true)")
    Page<RoommateListing> findFilteredRoommates(
            @Param("query") String query,
            @Param("bhk") String bhk,
            @Param("gender") String gender,
            @Param("diet") String diet,
            @Param("religion") String religion,
            @Param("min") Double min,
            @Param("max") Double max,
            @Param("furnish") boolean furnish,
            @Param("park") boolean park,
            @Param("water") boolean water,
            @Param("elec") boolean elec,
            Pageable pageable
    );
}