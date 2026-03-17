package com.yasirkhan.user.services.implementations;

import com.yasirkhan.user.exceptions.DatabaseException;
import com.yasirkhan.user.exceptions.ResourceNotFoundException;
import com.yasirkhan.user.models.dtos.UserEventDto;
import com.yasirkhan.user.models.entities.Role;
import com.yasirkhan.user.models.entities.Status;
import com.yasirkhan.user.models.entities.UsersProfile;
import com.yasirkhan.user.producer.UserEventProducer;
import com.yasirkhan.user.repositories.UserProfileRepository;
import com.yasirkhan.user.requests.UserRequest;
import com.yasirkhan.user.responses.AdminResponse;
import com.yasirkhan.user.services.AdminService;
import com.yasirkhan.user.utils.ResponseConversion;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserProfileRepository profileRepository;

    private final UserEventProducer eventProducer;


    public AdminServiceImpl(UserProfileRepository profileRepository, UserEventProducer eventProducer) {
        this.profileRepository = profileRepository;
        this.eventProducer = eventProducer;
    }

    @Override
    @Transactional
    public AdminResponse createAdmin(UserRequest request) {

        UsersProfile adminUser =
                new UsersProfile();
        adminUser.setName(request.getName());
        adminUser.setEmail(request.getEmail());
        adminUser.setPhoneNo(request.getPhoneNo());
        adminUser.setStatus(request.getIsBlocked()?Status.BLOCKED:Status.PENDING);
        UsersProfile savedUser
                = null;
        try {
            savedUser = profileRepository.save(adminUser);
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        // Send Event to Kafka
        UserEventDto eventDto =
                UserEventDto
                        .builder()
                        .userId(savedUser.getId())
                        .username(request.getUsername())
                        .email(savedUser.getEmail())
                        .password(request.getPassword())
                        .role(request.getRole())
                        .isBlocked(request.getIsBlocked())
                        .build();

        eventProducer.sendAuthUserCreateEvent(eventDto);

        return
                ResponseConversion
                        .toAdminResponse(
                                request.getUsername(),
                                request.getRole().name(),
                                savedUser);
    }

    @Override
    @Transactional
    public void updateAdmin(Map<String, Object> updateRequest) {

        UUID userId = UUID.fromString(updateRequest.get("userId").toString());

        UsersProfile dbUser =
                profileRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "User Not Found with User ID: " + userId));

        /* TODO: Use MapConstruct */
        UserEventDto eventDto =
                UserEventDto
                        .builder()
                        .userId(userId)
                        .build();

        updateRequest.forEach((key, value) ->
        {
            switch (key){
                case "email" -> {
                    dbUser.setEmail((String) value);
                    eventDto.setEmail((String) value);
                }
                case "username" -> {
                    eventDto.setUsername((String) value);
                }
                case "role" -> {
                    eventDto.setRole(Role.valueOf(value.toString()));
                }
                case "name" -> dbUser.setName((String) value);
                case "phoneNo" -> dbUser.setPhoneNo((String) value);
                case "status" -> dbUser.setStatus(Status.valueOf((String) value));
            }
        });

        profileRepository.save(dbUser);

        if (eventDto.getEmail() != null || eventDto.getUsername() != null || eventDto.getRole() != null) {
            eventProducer.sendAuthUserUpdateEvent(eventDto);
        }
    }

    @Override
    public AdminResponse getUserById(Map<String, Object> getRequest) {

        UUID userId = UUID.fromString((String) getRequest.get("userId"));
        String username = getRequest.get("username").toString();
        String role = getRequest.get("role").toString();

        UsersProfile dbUser =
                profileRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "User Not Found with User ID: " + userId));
        return ResponseConversion.toAdminResponse(username,role,dbUser);
    }

}
