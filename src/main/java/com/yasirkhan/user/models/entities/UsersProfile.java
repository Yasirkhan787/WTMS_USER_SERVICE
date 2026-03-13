package com.yasirkhan.user.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
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
public class UsersProfile {

    @Id
    private UUID id;

    private String name;

    private String email;

    private String phoneNo;

    @Enumerated(EnumType.STRING)
    private Status status;
}
