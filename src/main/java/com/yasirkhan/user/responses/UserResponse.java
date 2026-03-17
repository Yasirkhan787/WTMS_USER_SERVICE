package com.yasirkhan.user.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private String phoneNo;
    private String role;
    private String status;
    private DriverDetails driverDetails;
    private SupervisorDetails supervisorDetails;
}

