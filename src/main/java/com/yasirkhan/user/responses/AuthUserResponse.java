package com.yasirkhan.user.responses;

import com.yasirkhan.user.models.entities.Role;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserResponse {

    private UUID id;
    private String username;
    private String email;
    private Role role;
    private Boolean isBlocked;

}