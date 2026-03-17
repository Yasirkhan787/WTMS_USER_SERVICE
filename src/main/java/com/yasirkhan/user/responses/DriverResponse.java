package com.yasirkhan.user.responses;

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
public class DriverResponse {

    private UUID userID;
    private String username;
    private String email;
    private String role;
    private String name;
    private String fatherName;
    private String cnic;
    private String phoneNo;
    private String address;
    private String gender;
    private int age;
    private String licenseNo;
    private LocalDate licenseExpiry;
    private String status;
}
