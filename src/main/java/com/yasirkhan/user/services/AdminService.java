package com.yasirkhan.user.services;

import com.yasirkhan.user.models.dtos.UserEventDto;
import com.yasirkhan.user.requests.UserRequest;
import com.yasirkhan.user.responses.AdminResponse;

import java.util.Map;

public interface AdminService {
    void createAdmin(UserEventDto request);

    AdminResponse getUserById(Map<String, Object> getRequest);

    void updateAdmin(UserEventDto updateRequest);
}
