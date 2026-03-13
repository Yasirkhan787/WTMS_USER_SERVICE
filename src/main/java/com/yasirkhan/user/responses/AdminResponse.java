package com.yasirkhan.user.responses;

import com.yasirkhan.user.models.entities.Role;
import com.yasirkhan.user.models.entities.Status;
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
public class AdminResponse {

    private UUID userID;

    private String username;

    private String role;

    private String name;

    private String email;

    private String phoneNo;

    private String status;

}
