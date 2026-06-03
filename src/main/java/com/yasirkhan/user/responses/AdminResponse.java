package com.yasirkhan.user.responses;

import com.yasirkhan.user.models.enums.Role;
import com.yasirkhan.user.models.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponse {

    private UUID userID;
    private String username;
    private Role role;
    private String name;
    private String email;
    private String phoneNo;
    private Status status;

}
