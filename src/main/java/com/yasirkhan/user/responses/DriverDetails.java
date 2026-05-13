package com.yasirkhan.user.responses;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

// These DTOs capture the detailed fields from your Relational Models
@Data
@Builder
public class DriverDetails {
    private String fatherName;
    private String cnic;
    private String licenseNumber;
    private String licenseExpiry;
    private LocalDate dob;
    private String gender;
    private String address;
}
