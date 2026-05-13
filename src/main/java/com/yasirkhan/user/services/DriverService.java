package com.yasirkhan.user.services;

import com.yasirkhan.user.models.dtos.UserEventDto;
import com.yasirkhan.user.requests.UserRequest;
import com.yasirkhan.user.responses.DriverResponse;

import java.util.Map;

public interface DriverService {
    void createDriver(UserEventDto request);

    DriverResponse getUserById(Map<String, Object> getRequest);

    void updateDriver(UserEventDto updateRequest);
}
