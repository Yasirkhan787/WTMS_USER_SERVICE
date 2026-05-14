package com.yasirkhan.user.services.implementations;

import com.yasirkhan.user.exceptions.DatabaseException;
import com.yasirkhan.user.exceptions.ResourceNotFoundException;
import com.yasirkhan.user.models.dtos.UserEventDto;
import com.yasirkhan.user.models.dtos.UserStatusEventDto;
import com.yasirkhan.user.models.entities.Status;
import com.yasirkhan.user.models.entities.UsersProfile;
import com.yasirkhan.user.producer.UserEventProducer;
import com.yasirkhan.user.repositories.UserProfileRepository;
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
    public void createAdmin(UserEventDto request) {

        UsersProfile adminUser = new UsersProfile();

        adminUser.setId(request.getUserId());
        adminUser.setName(request.getName());
        adminUser.setEmail(request.getEmail());
        adminUser.setPhoneNo(request.getPhoneNo());
        adminUser.setStatus(Status.ACTIVE);

        try {
            profileRepository.saveAndFlush(adminUser);

            UserStatusEventDto successEvent = UserStatusEventDto.builder()
                    .userId(request.getUserId())
                    .status("SUCCESS")
                    .type("CREATE")
                    .build();

            eventProducer.sendUserCreatedStatusEvent(successEvent);

        } catch (Exception e) {

            UserStatusEventDto failureEvent = UserStatusEventDto.builder()
                    .userId(request.getUserId())
                    .status("FAILURE")
                    .type("CREATE")
                    .build();

            eventProducer.sendUserCreatedStatusEvent(failureEvent);

            throw new DatabaseException("Failed to save Admin Profile. Initiated Rollback. Error: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateAdmin(UserEventDto updateRequest) {

        UUID userId = updateRequest.getUserId();

        UsersProfile dbUser = profileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found with User ID: " + userId));

        if (updateRequest.getEmail() != null) dbUser.setEmail(updateRequest.getEmail());
        if (updateRequest.getName() != null) dbUser.setName(updateRequest.getName());
        if (updateRequest.getPhoneNo() != null) dbUser.setPhoneNo(updateRequest.getPhoneNo());
        if (updateRequest.getStatus() != null) dbUser.setStatus(Status.valueOf(updateRequest.getStatus()));

        profileRepository.save(dbUser);
    }

    @Override
    public AdminResponse getUserById(Map<String, Object> getRequest) {

        UUID userId = UUID.fromString((String) getRequest.get("userId"));
        String username = getRequest.get("username").toString();
        String role = getRequest.get("role").toString();

        UsersProfile dbUser = profileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found with User ID: " + userId));

        return ResponseConversion.toAdminResponse(username, role, dbUser);
    }
}