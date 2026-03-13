package com.yasirkhan.user.services.implementations;

import com.yasirkhan.user.models.entities.Role;
import com.yasirkhan.user.requests.UserRequest;
import com.yasirkhan.user.responses.AdminResponse;
import com.yasirkhan.user.responses.DriverResponse;
import com.yasirkhan.user.responses.SupervisorResponse;
import com.yasirkhan.user.services.AdminService;
import com.yasirkhan.user.services.DriverService;
import com.yasirkhan.user.services.SupervisorService;
import com.yasirkhan.user.services.UserService;
import com.yasirkhan.user.utils.ResourceHandler;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class UserServiceImpl implements UserService {

    private final ResourceHandler handler;

    public UserServiceImpl(ResourceHandler handler) {
        this.handler = handler;
    }

    @Override
    public Object addUser(UserRequest request) {
        return handler.addUser(request);
    }

    @Override
    public Object getUserById(String userId, String role) {

        Map<String, String> request = new HashMap<>();
        request.put("userId", userId);
        request.put("role", role);

        return handler.getUserById(request);
    }

   }
