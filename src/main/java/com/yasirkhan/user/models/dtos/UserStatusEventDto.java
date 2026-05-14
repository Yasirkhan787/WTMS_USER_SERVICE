package com.yasirkhan.user.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusEventDto {

    private UUID userId;
    private String status; // SUCCESS, FAILURE
    private String type; // CREATE , UPDATE

}
