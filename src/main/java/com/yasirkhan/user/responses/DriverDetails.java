package com.yasirkhan.user.responses;

import lombok.Builder;
import lombok.Data;

// These DTOs capture the detailed fields from your Relational Models [cite: 2566, 2567]
@Data
@Builder
public class DriverDetails {
    private String fatherName;
    private String cnic;
    private String licenseNumber;
    private String licenseExpiry;
    private Integer age;
    private String gender;
    private String address;
}
