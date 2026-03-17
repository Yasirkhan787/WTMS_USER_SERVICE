package com.yasirkhan.user.services;

import com.yasirkhan.user.models.entities.Status;
import com.yasirkhan.user.requests.UserRequest;
import com.yasirkhan.user.responses.UserResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UserService {
    Object addUser(UserRequest request);

    Object getUserById(String userId, String username, String role);

    UserResponse getUserById(String userId);

    void updateUser(Map<String, Object> updateRequest);

    void updateUserStatus(UUID userId, Status status);

    List<UserResponse> getAllUser();
}
