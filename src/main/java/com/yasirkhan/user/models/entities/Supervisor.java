package com.yasirkhan.user.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Supervisor {

    @Id
    private UUID id;

    private String fatherName;

    private String cnic;

    private int age;

    private String address;

    private String gender;

    @OneToOne()
    @MapsId
    @JoinColumn(name = "id")
    private UsersProfile profile;
}
