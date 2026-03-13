package com.yasirkhan.user.utils;

import com.yasirkhan.user.models.entities.Role;
import com.yasirkhan.user.requests.UserRequest;
import com.yasirkhan.user.responses.AdminResponse;
import com.yasirkhan.user.responses.DriverResponse;
import com.yasirkhan.user.responses.SupervisorResponse;
import com.yasirkhan.user.services.AdminService;
import com.yasirkhan.user.services.DriverService;
import com.yasirkhan.user.services.SupervisorService;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
public class ResourceHandler {

    private final Map<Role, Function<UserRequest, Object>> handleUserCreation =
            new EnumMap<>(Role.class);

    private final Map<Role, Function<UUID, Object>> handleGettingUser =
            new EnumMap<>(Role.class);

    private final AdminService adminService;

    private final SupervisorService supervisorService;

    private final DriverService driverService;

    public ResourceHandler(AdminService adminService, SupervisorService supervisorService, DriverService driverService){
        handleUserCreation.put(Role.ADMIN, this::handleAdminCreation);
        handleUserCreation.put(Role.SUPERVISOR, this::handleSupervisorCreation);
        handleUserCreation.put(Role.DRIVER, this::handleDriverCreation);

        handleGettingUser.put(Role.ADMIN, this::handleGetAdminUser);
        handleGettingUser.put(Role.SUPERVISOR, this::handleGetDriverUser);
        handleGettingUser.put(Role.DRIVER, this::handleGetSupervisorUser);

        this.adminService = adminService;
        this.supervisorService = supervisorService;
        this.driverService = driverService;
    }

    public Object addUser(UserRequest request) {

        Function<UserRequest, Object>  handler =
                handleUserCreation.get(request.getRole());

        if (handler == null){
            throw new RuntimeException("Invalid Role: " + request.getRole());
        }

        return handler.apply(request);
    }

    private AdminResponse handleAdminCreation(UserRequest request){
        return adminService.createAdmin(request);
    }

    private SupervisorResponse handleSupervisorCreation(UserRequest request){
        return supervisorService.createSupervisor(request);
    }

    private DriverResponse handleDriverCreation(UserRequest request){
        return driverService.createDriver(request);
    }



    public Object getUserById(Map<String, String> request) {

        UUID userId = UUID.fromString(request.get("userId"));

        Role role = Role.valueOf(request.get("role"));

        Function<UUID, Object> handler =
                handleGettingUser.get(role);

        if (handler == null){
            throw new RuntimeException("Invalid Role: " + role.name());
        }

        return handler.apply(userId);
    }

    private AdminResponse handleGetAdminUser(UUID userID){
        return adminService.getUserById(userID);
    }

    private SupervisorResponse handleGetSupervisorUser(UUID userID){

        return supervisorService.getUserById(userID);
    }

    private DriverResponse handleGetDriverUser(UUID userID){

        return driverService.getUserById(userID);
    }
}
