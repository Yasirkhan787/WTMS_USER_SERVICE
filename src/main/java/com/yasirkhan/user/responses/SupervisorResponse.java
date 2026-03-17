package com.yasirkhan.user.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupervisorResponse {

    private UUID userID;
    private String username;
    private String email;
    private String role;
    private String name;
    private String fatherName;
    private String cnic;
    private String phoneNo;
    private String address;
    private int age;
    private String gender;
    private String status;
}
