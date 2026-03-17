package com.yasirkhan.user.services.implementations;

import com.yasirkhan.user.exceptions.ResourceNotFoundException;
import com.yasirkhan.user.models.entities.Status;
import com.yasirkhan.user.models.entities.UsersProfile;
import com.yasirkhan.user.repositories.UserProfileRepository;
import com.yasirkhan.user.requests.UserRequest;
import com.yasirkhan.user.responses.UserResponse;
import com.yasirkhan.user.services.UserService;
import com.yasirkhan.user.utils.ResourceHandler;
import com.yasirkhan.user.utils.ResponseConversion;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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
                .findAllProfileWithDetails()
                .orElseThrow(() ->
                        new ResourceNotFoundException("No User Found in Database"));

        return users
                .stream()
                .map(ResponseConversion::toUserResponse)
                .collect(Collectors.toList());
    }
}
