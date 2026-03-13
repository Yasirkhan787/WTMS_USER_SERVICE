package com.yasirkhan.user.services;

import com.yasirkhan.user.requests.UserRequest;
import com.yasirkhan.user.responses.DriverResponse;

import java.util.UUID;

public interface DriverService {
    DriverResponse createDriver(UserRequest request);

    DriverResponse getUserById(UUID userID);
}
