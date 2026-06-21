package com.yasirkhan.user.models.entities;

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
public class Supervisor {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String fatherName;

    @Column(nullable = false, unique = true)
    private String cnic;

    @Column(nullable = false)
    private LocalDate dob;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String gender;

    private UUID tehsilId;

    private UUID yardId;

    @OneToOne()
    @MapsId
    @JoinColumn(name = "id")
    private UsersProfile profile;
}
