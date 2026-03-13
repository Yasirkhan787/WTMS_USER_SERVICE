package com.yasirkhan.user.services;

import com.yasirkhan.user.requests.UserRequest;
import com.yasirkhan.user.responses.SupervisorResponse;

import java.util.Map;
import java.util.UUID;

public interface SupervisorService {
    SupervisorResponse createSupervisor(UserRequest request);

    SupervisorResponse getUserById(Map<String, Object> getRequest);

    void updateSupervisor(Map<String, Object> updateRequest);
}
