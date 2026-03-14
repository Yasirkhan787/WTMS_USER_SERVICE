package com.yasirkhan.user.services.implementations;

import com.yasirkhan.user.models.entities.Role;
import com.yasirkhan.user.models.entities.Status;
import com.yasirkhan.user.models.entities.UsersProfile;
import com.yasirkhan.user.repositories.UserProfileRepository;
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
import java.util.UUID;
import java.util.function.Function;

@Service
public class UserServiceImpl implements UserService {

    private final ResourceHandler handler;

    private final UserProfileRepository profileRepository;

    public UserServiceImpl(ResourceHandler handler, UserProfileRepository profileRepository) {
        this.handler = handler;
        this.profileRepository = profileRepository;
    }

    @Override
    public Object addUser(UserRequest request) {
        return handler.addUser(request);
    }

    @Override
    public Object getUserById(String userId, String username, String role) {

        return handler.getUserById(username, userId, role);
    }

    @Override
    public void updateUser(Map<String, Object> updateRequest) {
        handler.updateUser(updateRequest);
    }

    @Override
    public void updateUserStatus(UUID userId, Status status) {

        UsersProfile dbUser =
                profileRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "User Not Found with User ID: " + userId));

        dbUser.setStatus(status);

        profileRepository.saveAndFlush(dbUser);
    }

}
