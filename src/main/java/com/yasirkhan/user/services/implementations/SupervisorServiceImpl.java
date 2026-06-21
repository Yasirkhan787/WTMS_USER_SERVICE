package com.yasirkhan.user.services.implementations;

import com.yasirkhan.user.exceptions.ResourceNotFoundException;
import com.yasirkhan.user.models.dtos.UserEventDto;
import com.yasirkhan.user.models.dtos.UserStatusEventDto;
import com.yasirkhan.user.models.entities.Supervisor;
import com.yasirkhan.user.models.entities.UsersProfile;
import com.yasirkhan.user.models.enums.EventStatus;
import com.yasirkhan.user.models.enums.EventType;
import com.yasirkhan.user.models.enums.Role;
import com.yasirkhan.user.models.enums.Status;
import com.yasirkhan.user.repositories.SupervisorRepository;
import com.yasirkhan.user.repositories.UserProfileRepository;
import com.yasirkhan.user.responses.SupervisorResponse;
import com.yasirkhan.user.services.SupervisorService;
import com.yasirkhan.user.utils.ResponseConversion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
public class SupervisorServiceImpl implements SupervisorService {

    private final UserProfileRepository profileRepository;
    private final SupervisorRepository supervisorRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RedisTemplate<String, Object> redisTemplate;

    public SupervisorServiceImpl(UserProfileRepository profileRepository, SupervisorRepository supervisorRepository, ApplicationEventPublisher eventPublisher, RedisTemplate<String, Object> redisTemplate) {
        this.profileRepository = profileRepository;
        this.supervisorRepository = supervisorRepository;
        this.eventPublisher = eventPublisher;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional
    public void createSupervisor(UserEventDto request) {

        if (supervisorRepository.existsByCnic(request.getCnic())) {
            publishFailureEvent(request, EventType.CREATE, "Duplicate CNIC");
            return;
        }

        UsersProfile supervisorProfile = new UsersProfile();
        supervisorProfile.setId(request.getUserId());
        supervisorProfile.setName(request.getName());
        supervisorProfile.setEmail(request.getEmail());
        supervisorProfile.setPhoneNo(request.getPhoneNo());
        supervisorProfile.setStatus(Status.ACTIVE);

        try {

            profileRepository.saveAndFlush(supervisorProfile);

            Supervisor supervisor = Supervisor.builder()
                    .fatherName(request.getFatherName())
                    .cnic(request.getCnic())
                    .address(request.getAddress())
                    .gender(request.getGender())
                    .dob(request.getDob())
                    .tehsilId(request.getTehsilId())
                    .yardId(request.getYardId())
                    .profile(supervisorProfile)
                    .build();

            supervisorRepository.saveAndFlush(supervisor);

            // Sync Base Profile to local Redis
            syncUserToRedis(supervisorProfile, supervisor, Role.SUPERVISOR);

            publishSuccessEvent(request, EventType.CREATE);

        } catch (Exception e) {
            publishFailureEvent(request, EventType.CREATE, "Database Error: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateSupervisor(UserEventDto updateRequest) {
        UUID userId = updateRequest.getUserId();

        Optional<UsersProfile> dbUserOpt = profileRepository.findById(userId);
        Optional<Supervisor> dbSupervisorOpt = supervisorRepository.findById(userId);

        if (dbUserOpt.isEmpty() || dbSupervisorOpt.isEmpty()) {
            publishFailureEvent(updateRequest, EventType.UPDATE, "User or Supervisor Not Found");
            return;
        }

        UsersProfile dbUser = dbUserOpt.get();
        Supervisor dbSupervisor = dbSupervisorOpt.get();

        if (updateRequest.getEmail() != null) dbUser.setEmail(updateRequest.getEmail());
        if (updateRequest.getName() != null) dbUser.setName(updateRequest.getName());
        if (updateRequest.getPhoneNo() != null) dbUser.setPhoneNo(updateRequest.getPhoneNo());
        if (updateRequest.getStatus() != null) dbUser.setStatus(Status.valueOf(updateRequest.getStatus()));

        if (updateRequest.getFatherName() != null) dbSupervisor.setFatherName(updateRequest.getFatherName());

        if (updateRequest.getCnic() != null) {
            if (!updateRequest.getCnic().equals(dbSupervisor.getCnic()) && supervisorRepository.existsByCnic(updateRequest.getCnic())) {
                publishFailureEvent(updateRequest, EventType.UPDATE, "CNIC already exists");
                return;
            }
            dbSupervisor.setCnic(updateRequest.getCnic());
        }
        if (updateRequest.getTehsilId() != null) dbSupervisor.setTehsilId(updateRequest.getTehsilId());
        if (updateRequest.getYardId() != null) dbSupervisor.setYardId(updateRequest.getYardId());
        if (updateRequest.getGender() != null) dbSupervisor.setGender(updateRequest.getGender());
        if (updateRequest.getAddress() != null) dbSupervisor.setAddress(updateRequest.getAddress());
        if (updateRequest.getDob() != null) dbSupervisor.setDob(updateRequest.getDob());

        try {
            supervisorRepository.save(dbSupervisor);
            profileRepository.save(dbUser);

            syncUserToRedis(dbUser, dbSupervisor, Role.SUPERVISOR);

            publishSuccessEvent(updateRequest, EventType.UPDATE);
        } catch (Exception e) {
            publishFailureEvent(updateRequest, EventType.UPDATE, "Database Error: " + e.getMessage());
        }
    }

    @Override
    public SupervisorResponse getUserById(Map<String, Object> getRequest) {
        UUID userId = UUID.fromString((String) getRequest.get("userId"));
        String username = getRequest.get("username").toString();
        String role = getRequest.get("role").toString();

        UsersProfile dbUser = profileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found with User ID: " + userId));

        Supervisor dbSupervisor = supervisorRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Supervisor Not Found with User ID: " + userId));

        return ResponseConversion.toSupervisorResponse(username, role, dbUser, dbSupervisor);
    }

    // --- Updated Redis Sync Helper (Option 2) ---
    private void syncUserToRedis(UsersProfile profile, Supervisor supervisor, Role role) {
        String redisKey = "wtms:user:" + profile.getId();
        Map<String, Object> cacheData = new HashMap<>();

        // 1. Pack the standard Profile Data
        cacheData.put("name", profile.getName());
        cacheData.put("email", profile.getEmail());
        cacheData.put("phoneNo", profile.getPhoneNo());
        cacheData.put("status", profile.getStatus() != null ? profile.getStatus().name() : "");
        cacheData.put("role", role.name());

        // 2. Pack the specific Supervisor Data directly from the passed object
        if (supervisor != null) {
            cacheData.put("fatherName", supervisor.getFatherName());
            cacheData.put("cnic", supervisor.getCnic());
            cacheData.put("gender", supervisor.getGender());
            cacheData.put("address", supervisor.getAddress());

            // Convert Dates and UUIDs to Strings for Redis safety
            cacheData.put("dob", supervisor.getDob() != null ? supervisor.getDob().toString() : "");
            cacheData.put("tehsilId", supervisor.getTehsilId() != null ? supervisor.getTehsilId().toString() : "");

            // Adding yardId as well since it's part of your Supervisor entity
            cacheData.put("yardId", supervisor.getYardId() != null ? supervisor.getYardId().toString() : "");
        }

        // 3. Save the combined map to Redis
        redisTemplate.opsForHash().putAll(redisKey, cacheData);
    }

    // --- HELPER METHODS FOR SAGA EVENTS ---

    private void publishSuccessEvent(UserEventDto sourceData, EventType type) {

        UserEventDto supervisorData = UserEventDto.builder()
                .userId(sourceData.getUserId())
                .name(sourceData.getName())
                .fatherName(sourceData.getFatherName())
                .email(sourceData.getEmail())
                .dob(sourceData.getDob())
                .address(sourceData.getAddress())
                .cnic(sourceData.getCnic())
                .gender(sourceData.getGender())
                .phoneNo(sourceData.getPhoneNo())
                .role(Role.SUPERVISOR)
                .tehsilId(sourceData.getTehsilId())
                .status(Status.ACTIVE.name())
                .build();

        UserStatusEventDto eventDto = UserStatusEventDto.builder()
                .type(type)
                .eventTypeStatus(EventStatus.SUCCESS)
                .userData(supervisorData)
                .build();

        eventPublisher.publishEvent(eventDto);
    }

    private void publishFailureEvent(UserEventDto sourceData, EventType type, String reason) {
        log.error("Supervisor Saga Event Failed. Reason: {}", reason);

        UserEventDto payloadData = UserEventDto.builder()
                .userId(sourceData.getUserId())
                .status("BLOCK")
                .role(Role.SUPERVISOR)
                .build();

        UserStatusEventDto eventDto = UserStatusEventDto.builder()
                .type(type)
                .eventTypeStatus(EventStatus.FAILURE)
                .userData(payloadData)
                .build();

        eventPublisher.publishEvent(eventDto);
    }
}