package com.yasirkhan.user.utils;

import com.yasirkhan.user.models.dtos.UserEventDto;
import com.yasirkhan.user.models.entities.Role;
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

    private final Map<Role, Consumer<UserEventDto>> handleUserCreation = new EnumMap<>(Role.class);

    private final Map<Role, Consumer<UserEventDto>> handleUserUpdation = new EnumMap<>(Role.class);

    private final Map<Role, Function<Map<String, Object>, Object>> handleGettingUser = new EnumMap<>(Role.class);

    private final AdminService adminService;
    private final SupervisorService supervisorService;
    private final DriverService driverService;

    public ResourceHandler(AdminService adminService, SupervisorService supervisorService, DriverService driverService){
        handleUserCreation.put(Role.ADMIN, this::handleAdminCreation);
        handleUserCreation.put(Role.SUPERVISOR, this::handleSupervisorCreation);
        handleUserCreation.put(Role.DRIVER, this::handleDriverCreation);

        handleUserUpdation.put(Role.ADMIN, this::handleAdminUpdation);
        handleUserUpdation.put(Role.SUPERVISOR, this::handleSupervisorUpdation);
        handleUserUpdation.put(Role.DRIVER, this::handleDriverUpdation);

        handleGettingUser.put(Role.ADMIN, this::handleGetAdminUser);
        handleGettingUser.put(Role.SUPERVISOR, this::handleGetSupervisorUser);
        handleGettingUser.put(Role.DRIVER, this::handleGetDriverUser);

        this.adminService = adminService;
        this.supervisorService = supervisorService;
        this.driverService = driverService;
    }

    public void addUser(UserEventDto addRequest) {

        Consumer<UserEventDto> handler = handleUserCreation.get(addRequest.getRole());

        if (handler == null){
            throw new RuntimeException("Invalid Role: " + addRequest.getRole());
        }

        handler.accept(addRequest);
    }

    public void updateUser(UserEventDto updateRequest) {

        Role role = updateRequest.getRole();

        Consumer<UserEventDto> updateHandler = handleUserUpdation.get(role);

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

    private void handleAdminCreation(UserEventDto addRequest){
        adminService.createAdmin(addRequest);
    }
    private void handleSupervisorCreation(UserEventDto addRequest){
        supervisorService.createSupervisor(addRequest);
    }
    private void handleDriverCreation(UserEventDto addRequest){
        driverService.createDriver(addRequest);
    }

    private void handleAdminUpdation(UserEventDto updateRequest) {
        adminService.updateAdmin(updateRequest);
    }
    private void handleSupervisorUpdation(UserEventDto updateRequest) {
        supervisorService.updateSupervisor(updateRequest);
    }
    private void handleDriverUpdation(UserEventDto updateRequest) {
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