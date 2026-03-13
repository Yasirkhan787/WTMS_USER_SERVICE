package com.yasirkhan.user.requests;

import com.yasirkhan.user.models.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    private String username;

    private String email;

    private String password;

    private Role role;

    private Boolean isBlocked;

    private String name;

    private String fatherName;

    private String cnic;

    private String phoneNo;

    private String address;

    private String gender;

    private String licenseNo;

    private LocalDate licenseExpiry;
}
