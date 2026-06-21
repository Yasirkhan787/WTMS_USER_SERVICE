package com.yasirkhan.user.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

// These DTOs capture the detailed fields from your Relational Models
@Data
@Builder
public class DriverDetails {
    private String fatherName;
    private String cnic;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate dob;
    private String gender;
    private String address;
    private String licenseNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate licenseExpiry;
    private String licenseStatus;
    private UUID tehsilId;
}
