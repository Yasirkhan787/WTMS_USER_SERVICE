package com.yasirkhan.user.services.implementations;

import com.yasirkhan.user.exceptions.ResourceNotFoundException;
import com.yasirkhan.user.models.dtos.UserEventDto;
import com.yasirkhan.user.models.entities.Status;
import com.yasirkhan.user.models.entities.UsersProfile;
import com.yasirkhan.user.repositories.UserProfileRepository;
import com.yasirkhan.user.responses.UserResponse;
import com.yasirkhan.user.services.UserService;
import com.yasirkhan.user.utils.ResourceHandler;
import com.yasirkhan.user.utils.ResponseConversion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final ResourceHandler handler;

    private final UserProfileRepository profileRepository;

    public UserServiceImpl(ResourceHandler handler, UserProfileRepository profileRepository) {
        this.handler = handler;
        this.profileRepository = profileRepository;
    }

    @Override
    public void addUser(UserEventDto request) {
        handler.addUser(request);
    }

    @Override
    public Object getUserById(String userId, String username, String role) {

        return handler.getUserById(username, userId, role);
    }

    @Override
    public void updateUser(UserEventDto updateRequest) {
        handler.updateUser(updateRequest);
    }

    @Override
    public void updateUserStatus(UUID userId, Status status) {

        UsersProfile dbUser =
                profileRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "User Not Found with User ID: " + userId));

        dbUser.setStatus(status);

        profileRepository.saveAndFlush(dbUser);
    }

    @Override
    public UserResponse getUserById(String userId) {

        UUID userID = UUID.fromString(userId);
        UsersProfile usersProfile =
                profileRepository.findProfileWithDetails(userID)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "User Not Found with User ID: " + userId));

        return ResponseConversion.toUserResponse(usersProfile);
    }

    @Override
    public List<UserResponse> getAllUser() {

        List<UsersProfile> users = profileRepository
                .findAllProfileWithDetails();

        return users
                .stream()
                .map(ResponseConversion::toUserResponse)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional(readOnly = true) // Best practice for GET methods: improves Hibernate performance
    public List<UserResponse> getAllDrivers() {

        // Returns a populated list, or an empty list [] if no drivers exist
        List<UsersProfile> drivers = profileRepository.findAllDrivers();

        return drivers
                .stream()
                .map(ResponseConversion::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllSupervisors() {

        List<UsersProfile> supervisors = profileRepository.findAllSupervisors();

        return supervisors.stream()
                .map(ResponseConversion::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllAdmins() {

        List<UsersProfile> admins = profileRepository.findAllAdmins();

        return admins.stream()
                .map(ResponseConversion::toUserResponse)
                .collect(Collectors.toList());
    }
}
