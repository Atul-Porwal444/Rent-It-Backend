package com.rentit.entity.post;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
@Data
public abstract class BaseListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // this will create a table containing two field listId as F.K and image url
    @ElementCollection
    @CollectionTable(
            joinColumns = @JoinColumn(name = "listing_id")
    )
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    private String location;
    private String city;
    private String description;

    private String bhkType;

    private int floorNumber;
    private boolean hasParking;
    private boolean isFurnished;
    private boolean waterSupply24x7;
    private boolean electricityBackup;

    private double rentAmount;

    private LocalDate postedOn;

    @PrePersist
    public void onCreate() {
        this.postedOn = LocalDate.now();
    }
}
