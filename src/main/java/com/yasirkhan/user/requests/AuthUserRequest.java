package com.yasirkhan.user.requests;

import com.yasirkhan.user.models.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserRequest {

    private String username;

    private String email;

    private String password;

    private Role role;

    private Boolean isBlocked;
}