package com.yasirkhan.user.services;

import com.yasirkhan.user.models.dtos.UserEventDto;
import com.yasirkhan.user.responses.SupervisorResponse;

import java.util.Map;

public interface SupervisorService {
    void createSupervisor(UserEventDto request);

    SupervisorResponse getUserById(Map<String, Object> getRequest);

    void updateSupervisor(UserEventDto updateRequest);
}
