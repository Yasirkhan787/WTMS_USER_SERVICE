package com.yasirkhan.user.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SupervisorDetails {
    private String fatherName;
    private String cnic;
    private String address;
    private Integer age;
    private String gender;
}
