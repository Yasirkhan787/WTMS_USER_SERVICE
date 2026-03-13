package com.yasirkhan.user.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Driver {

    @Id
    private UUID id;

    private String fatherName;

    private String cnic;

    private String address;

    private String gender;

    private String licenseNo;

    private LocalDate licenseExpiry;

    @OneToOne()
    @MapsId
    @JoinColumn(name = "id")
    private UserProfile profile;
}
