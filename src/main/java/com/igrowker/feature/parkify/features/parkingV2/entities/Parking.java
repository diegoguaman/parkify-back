package com.igrowker.feature.parkify.features.parkingV2.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.igrowker.feature.parkify.features.auth.entities.AuthUser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "ParkingV2")
@Table(name = "parking_v2")
public class Parking {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private AuthUser owner;

    @Column(nullable = false)
    private String parkingName;

    @Column(nullable = false)
    private String parkingAddress;

    @Column(nullable = false)
    private String parkingPhone;

    @Column(nullable = true)
    private String imageUrl;

    @Column(nullable = false)
    private int totalSpots;

    @Column(nullable = false)
    private int availableSpots;

    @Builder.Default
    @ElementCollection
    private List<String> extraFeatures = new ArrayList<>();

    @Column(nullable = true)
    private Double ratingAvg;

    @Column(nullable = true)
    private Integer ratingCount;

    @Column(nullable = false)
    private Double lat;

    @Column(nullable = false)
    private Double lng;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_type")
    private AccessType accessType;

    @Column(name = "access_instructions")
    private String accessInstructions;

    @Builder.Default
    @OneToMany(mappedBy = "parking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Shift> shifts = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
