package com.yasirkhan.user.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
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

    private String address;

    private String gender;

    @OneToOne()
    private UserProfile profile;
}
