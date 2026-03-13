package com.yasirkhan.user.services;

import com.yasirkhan.user.requests.UserRequest;

import java.util.Map;
import java.util.UUID;

public interface UserService {
    Object addUser(UserRequest request);

    Object getUserById(String userId, String username, String role);

    void updateUser(Map<String, Object> updateRequest);
}
