package com.yasirkhan.user.services.implementations;

import com.yasirkhan.user.exceptions.ResourceNotFoundException;
import com.yasirkhan.user.models.dtos.UserEventDto;
import com.yasirkhan.user.models.dtos.UserStatusEventDto;
import com.yasirkhan.user.models.entities.UsersProfile;
import com.yasirkhan.user.models.enums.EventStatus;
import com.yasirkhan.user.models.enums.EventType;
import com.yasirkhan.user.models.enums.Role;
import com.yasirkhan.user.models.enums.Status;
import com.yasirkhan.user.repositories.UserProfileRepository;
import com.yasirkhan.user.responses.AdminResponse;
import com.yasirkhan.user.services.AdminService;
import com.yasirkhan.user.utils.ResponseConversion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {

    private final UserProfileRepository profileRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RedisTemplate<String, Object> redisTemplate;

    public AdminServiceImpl(UserProfileRepository profileRepository,
                            ApplicationEventPublisher eventPublisher,
                            RedisTemplate<String, Object> redisTemplate) {
        this.profileRepository = profileRepository;
        this.eventPublisher = eventPublisher;
        this.redisTemplate = redisTemplate;
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
            UsersProfile usersProfile = profileRepository.saveAndFlush(adminUser);

            syncUserToRedis(usersProfile, Role.ADMIN);

            publishSuccessEvent(usersProfile, EventType.CREATE);
        } catch (Exception e) {
            publishFailureEvent(request, EventType.CREATE, "Database Error: " + e.getMessage());
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

        try {
            UsersProfile usersProfile = profileRepository.saveAndFlush(dbUser);

            syncUserToRedis(usersProfile, Role.ADMIN);

            publishSuccessEvent(usersProfile, EventType.UPDATE);
        } catch (Exception e) {
            publishFailureEvent(updateRequest, EventType.UPDATE, "Database Error: " + e.getMessage());
        }
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

    // --- HELPER METHODS ---
    private void syncUserToRedis(UsersProfile profile, Role role) {
        String redisKey = "wtms:user:" + profile.getId();
        Map<String, Object> cacheData = new HashMap<>();
        cacheData.put("name", profile.getName());
        cacheData.put("email", profile.getEmail());
        cacheData.put("phoneNo", profile.getPhoneNo());
        cacheData.put("status", profile.getStatus().name());
        cacheData.put("role", role.name());
        redisTemplate.opsForHash().putAll(redisKey, cacheData);
    }

    private void publishSuccessEvent(UsersProfile sourceData, EventType type) {
        UserEventDto adminData = UserEventDto.builder()
                .userId(sourceData.getId()) // BUG FIXED: SAGA needs this ID to unlock auth!
                .name(sourceData.getName())
                .email(sourceData.getEmail())
                .phoneNo(sourceData.getPhoneNo())
                .role(Role.ADMIN)
                .status(sourceData.getStatus().name())
                .build();

        UserStatusEventDto eventDto = UserStatusEventDto.builder()
                .type(type)
                .eventTypeStatus(EventStatus.SUCCESS)
                .userData(adminData)
                .build();
        eventPublisher.publishEvent(eventDto);
    }

    private void publishFailureEvent(UserEventDto sourceData, EventType type, String reason) {
        log.error("Admin Saga Event Failed. Reason: {}", reason);
        UserEventDto payloadData = UserEventDto.builder()
                .userId(sourceData.getUserId())
                .status("BLOCK")
                .role(Role.ADMIN)
                .build();
        UserStatusEventDto eventDto = UserStatusEventDto.builder()
                .type(type)
                .eventTypeStatus(EventStatus.FAILURE)
                .userData(payloadData)
                .build();
        eventPublisher.publishEvent(eventDto);
    }
}