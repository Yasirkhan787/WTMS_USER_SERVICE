package com.yasirkhan.user.responses;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class SupervisorDetails {
    private String fatherName;
    private String cnic;
    private String address;
    private LocalDate dob;
    private String gender;
}
