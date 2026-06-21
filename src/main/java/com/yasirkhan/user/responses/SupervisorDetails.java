package com.yasirkhan.user.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class SupervisorDetails {
    private String fatherName;
    private String cnic;
    private String address;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate dob;
    private String gender;
    private UUID tehsilId;
    private UUID yardId;
}
