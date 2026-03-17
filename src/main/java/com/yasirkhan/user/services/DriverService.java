package com.yasirkhan.user.services;

import com.yasirkhan.user.requests.UserRequest;
import com.yasirkhan.user.responses.DriverResponse;

import java.util.Map;

public interface DriverService {
    DriverResponse createDriver(UserRequest request);

    DriverResponse getUserById(Map<String, Object> getRequest);

    void updateDriver(Map<String, Object> updateRequest);
}
