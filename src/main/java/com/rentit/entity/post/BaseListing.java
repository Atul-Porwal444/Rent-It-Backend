package com.rentit.entity.post;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // this will store the image url in the array
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "image_url", columnDefinition = "jsonb")
    private List<String> imageUrls = new ArrayList<>();

    private String location;
    private String city;
    private String state;

    @Column(length = 6)
    private String pincode;

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
