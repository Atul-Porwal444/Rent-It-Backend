package com.rentit.repository.post;

import com.rentit.entity.post.RoomListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomListingRepository extends JpaRepository<RoomListing, Long> {

    // Help to filter the rooms that are from the particular city
    List<RoomListing> findByCity(String city);

    // Help to filter the rooms with price filter
    List<RoomListing> findByRentAmountLessThanEqual(double maxPrice);

    // Help to filter the rooms based on the need for the BKHs
    List<RoomListing> findByBhkTypeAndAvailabilityStatus(String bhkType, String availabilityStatus);

    // Help to filter the rooms with city and amount
    List<RoomListing> findByCityAndRentAmountLessThanEqual(String city, double maxPrice);
}
