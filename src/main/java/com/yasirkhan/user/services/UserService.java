package com.yasirkhan.user.services;

import com.yasirkhan.user.models.dtos.UserEventDto;
import com.yasirkhan.user.models.enums.Status;
import com.yasirkhan.user.responses.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {
    void addUser(UserEventDto addRequest);

    Object getUserById(String userId, String username, String role);

    UserResponse getUserById(String userId);

    void updateUser(UserEventDto updateRequest);

    void updateUserStatus(UUID userId, Status status);

    List<UserResponse> getAllUser();

    List<UserResponse> getAllAdmins();

    List<UserResponse> getAllSupervisors();

    List<UserResponse> getAllDrivers();
}
