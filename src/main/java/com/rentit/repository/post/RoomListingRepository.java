package com.rentit.repository.post;

import com.rentit.entity.post.RoomListing;
import com.rentit.entity.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomListingRepository extends JpaRepository<RoomListing, Long> {

    @EntityGraph(attributePaths = {"imageUrls", "user"})
    Optional<RoomListing> findById(Long id);

    Optional<RoomListing> readById(Long id);

    @EntityGraph(attributePaths = {"imageUrls", "user"})
    @Query("SELECT r FROM RoomListing r WHERE " +
            "(:query IS NULL OR :query = '' OR LOWER(r.city) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(r.location) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND (:bhk IS NULL OR :bhk = '' OR r.bhkType = :bhk) " +
            "AND (:min IS NULL OR r.rentAmount >= :min) " +
            "AND (:max IS NULL OR r.rentAmount <= :max) " +
            "AND (:furnish = false OR r.isFurnished = true) " +
            "AND (:park = false OR r.hasParking = true) " +
            "AND (:water = false OR r.waterSupply24x7 = true) " +
            "AND (:elec = false OR r.electricityBackup = true)")
    Page<RoomListing> findFilteredRooms(
            @Param("query") String query,
            @Param("bhk") String bhk,
            @Param("min") Double min,
            @Param("max") Double max,
            @Param("furnish") boolean furnish,
            @Param("park") boolean park,
            @Param("water") boolean water,
            @Param("elec") boolean elec,
            Pageable pageable
    );

    List<RoomListing> findByUser(UserEntity user);
}
