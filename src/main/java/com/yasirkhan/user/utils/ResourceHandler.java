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
import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class ResourceHandler {

    private final Map<Role, Function<UserRequest, Object>> handleUserCreation =
            new EnumMap<>(Role.class);

    private final Map<Role, Consumer<Map<String, Object>>> handleUserUpdation =
            new EnumMap<>(Role.class);

    private final Map<Role, Function<Map<String, Object>, Object>> handleGettingUser =
            new EnumMap<>(Role.class);

    private final AdminService adminService;

    private final SupervisorService supervisorService;

    private final DriverService driverService;

    public ResourceHandler(AdminService adminService, SupervisorService supervisorService, DriverService driverService){
        handleUserCreation.put(Role.ADMIN, this::handleAdminCreation);
        handleUserCreation.put(Role.SUPERVISOR, this::handleSupervisorCreation);
        handleUserCreation.put(Role.DRIVER, this::handleDriverCreation);

        handleUserUpdation.put(Role.ADMIN, this::handleAdminUpdation);
        handleUserUpdation.put(Role.SUPERVISOR, this::handleDriverUpdation);
        handleUserUpdation.put(Role.DRIVER, this::handleSupervisorUpdation);

        handleGettingUser.put(Role.ADMIN, this::handleGetAdminUser);
        handleGettingUser.put(Role.SUPERVISOR, this::handleGetDriverUser);
        handleGettingUser.put(Role.DRIVER, this::handleGetSupervisorUser);

        this.adminService = adminService;
        this.supervisorService = supervisorService;
        this.driverService = driverService;
    }

    public Object addUser(UserRequest addRequest) {

        Function<UserRequest, Object>  handler =
                handleUserCreation.get(addRequest.getRole());

        if (handler == null){
            throw new RuntimeException("Invalid Role: " + addRequest.getRole());
        }

        return handler.apply(addRequest);
    }

    public void updateUser(Map<String, Object> updateRequest) {

        Role role = Role.valueOf((String) updateRequest.get("role"));

        Consumer<Map<String, Object>> updateHandler = handleUserUpdation.get(role);

        if (updateHandler == null){
            throw new RuntimeException("Invalid Role: " + role.name());
        }

        updateHandler.accept(updateRequest);
    }

    public Object getUserById(String userId, String username, String role) {

        Map<String, Object> getRequest = new HashMap<>();
        getRequest.put("userId", userId);
        getRequest.put("username", username);
        getRequest.put("role", role);

        Role userRole = Role.valueOf(role);

        Function<Map<String, Object>, Object> handler =
                handleGettingUser.get(userRole);

        if (handler == null){
            throw new RuntimeException("Invalid Role: " + role);
        }

        return handler.apply(getRequest);
    }

    private AdminResponse handleAdminCreation(UserRequest addRequest){
        return adminService.createAdmin(addRequest);
    }

    private SupervisorResponse handleSupervisorCreation(UserRequest addRequest){
        return supervisorService.createSupervisor(addRequest);
    }

    private DriverResponse handleDriverCreation(UserRequest addRequest){
        return driverService.createDriver(addRequest);
    }

    private void handleAdminUpdation(Map<String, Object> updateRequest) {
        adminService.updateAdmin(updateRequest);
    }

    private void handleSupervisorUpdation(Map<String, Object> updateRequest) {
        supervisorService.updateSupervisor(updateRequest);
    }

    private void handleDriverUpdation(Map<String, Object> updateRequest) {
        driverService.updateDriver(updateRequest);
    }

    private AdminResponse handleGetAdminUser(Map<String, Object> getRequest){
        return adminService.getUserById(getRequest);
    }

    private SupervisorResponse handleGetSupervisorUser(Map<String, Object> getRequest){
        return supervisorService.getUserById(getRequest);
    }

    private DriverResponse handleGetDriverUser(Map<String, Object> getRequest){
        return driverService.getUserById(getRequest);
    }
}
