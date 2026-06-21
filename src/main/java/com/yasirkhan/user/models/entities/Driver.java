package com.yasirkhan.user.models.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Driver {

    @Id
    private UUID id;

    private UUID tehsilId;

    @Column(nullable = false)
    private String fatherName;

    @Column(nullable = false, unique = true)
    private String cnic;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private LocalDate dob;

    @Column(nullable = false)
    private String licenseNo;

    @Column(nullable = false)
    private LocalDate licenseExpiry;

    @OneToOne()
    @MapsId
    @JoinColumn(name = "id")
    private UsersProfile profile;
}
