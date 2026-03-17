package com.yasirkhan.user.services;

import com.yasirkhan.user.requests.UserRequest;
import com.yasirkhan.user.responses.AdminResponse;

import java.util.Map;

public interface AdminService {
    AdminResponse createAdmin(UserRequest request);

    AdminResponse getUserById(Map<String, Object> getRequest);

    void updateAdmin(Map<String, Object> updateRequest);
}
