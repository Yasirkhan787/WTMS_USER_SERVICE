package com.yasirkhan.user.services;

import com.yasirkhan.user.requests.UserRequest;

public interface UserService {
    Object addUser(UserRequest request);

    Object getUserById(String userId, String role);
}
