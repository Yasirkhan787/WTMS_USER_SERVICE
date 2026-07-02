package com.yasirkhan.user.services.implementations;

import com.yasirkhan.user.exceptions.ResourceNotFoundException;
import com.yasirkhan.user.models.dtos.UserEventDto;
import com.yasirkhan.user.models.dtos.UserStatusEventDto;
import com.yasirkhan.user.models.entities.Driver;
import com.yasirkhan.user.models.entities.UsersProfile;
import com.yasirkhan.user.models.enums.EventStatus;
import com.yasirkhan.user.models.enums.EventType;
import com.yasirkhan.user.models.enums.Role;
import com.yasirkhan.user.models.enums.Status;
import com.yasirkhan.user.repositories.DriverRepository;
import com.yasirkhan.user.repositories.UserProfileRepository;
import com.yasirkhan.user.responses.DriverResponse;
import com.yasirkhan.user.services.DriverService;
import com.yasirkhan.user.utils.ResponseConversion;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class DriverServiceImpl implements DriverService {

    private final UserProfileRepository profileRepository;
    private final DriverRepository driverRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RedisTemplate<String, Object> redisTemplate;

    public DriverServiceImpl(UserProfileRepository profileRepository, DriverRepository driverRepository, ApplicationEventPublisher eventPublisher, RedisTemplate<String, Object> redisTemplate) {
        this.profileRepository = profileRepository;
        this.driverRepository = driverRepository;
        this.eventPublisher = eventPublisher;
        this.redisTemplate = redisTemplate;
    }

    @Override
    @Transactional
    public void createDriver(UserEventDto request) {

        if (driverRepository.existsByCnic(request.getCnic()) ||
                driverRepository.existsByLicenseNo(request.getLicenseNo())) {
            publishFailureEvent(request, EventType.CREATE, "Duplicate CNIC or License No");
            return;
        }

        UsersProfile driverProfile = new UsersProfile();
        driverProfile.setId(request.getUserId());
        driverProfile.setName(request.getName());
        driverProfile.setEmail(request.getEmail());
        driverProfile.setPhoneNo(request.getPhoneNo());
        driverProfile.setStatus(Status.ACTIVE);

        try {
            profileRepository.saveAndFlush(driverProfile);

            Driver driver = Driver.builder()
                    .tehsilId(request.getTehsilId())
                    .fatherName(request.getFatherName())
                    .cnic(request.getCnic())
                    .address(request.getAddress())
                    .gender(request.getGender())
                    .dob(request.getDob())
                    .licenseNo(request.getLicenseNo())
                    .licenseExpiry(request.getLicenseExpiry())
                    .profile(driverProfile)
                    .build();

            driverRepository.saveAndFlush(driver);

            syncUserToRedis(driverProfile, driver, Role.DRIVER);

            publishSuccessEvent(request, EventType.CREATE);

        } catch (Exception e) {
            publishFailureEvent(request, EventType.CREATE, "Database Error: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateDriver(UserEventDto updateRequest) {
        UUID userId = updateRequest.getUserId();

        Optional<UsersProfile> dbUserOpt = profileRepository.findById(userId);
        Optional<Driver> dbDriverOpt = driverRepository.findById(userId);

        if (dbUserOpt.isEmpty() || dbDriverOpt.isEmpty()) {
            publishFailureEvent(updateRequest, EventType.UPDATE, "User or Driver Not Found");
            return;
        }

        UsersProfile dbUser = dbUserOpt.get();
        Driver dbDriver = dbDriverOpt.get();

        if (updateRequest.getEmail() != null) dbUser.setEmail(updateRequest.getEmail());
        if (updateRequest.getName() != null) dbUser.setName(updateRequest.getName());
        if (updateRequest.getPhoneNo() != null) dbUser.setPhoneNo(updateRequest.getPhoneNo());
        if (updateRequest.getStatus() != null) dbUser.setStatus(Status.valueOf(updateRequest.getStatus()));

        if (updateRequest.getFatherName() != null) dbDriver.setFatherName(updateRequest.getFatherName());

        if (updateRequest.getCnic() != null) {
            if (!updateRequest.getCnic().equals(dbDriver.getCnic()) && driverRepository.existsByCnic(updateRequest.getCnic())) {
                publishFailureEvent(updateRequest, EventType.UPDATE, "CNIC already exists");
                return;
            }
            dbDriver.setCnic(updateRequest.getCnic());
        }

        if (updateRequest.getTehsilId() != null) dbDriver.setTehsilId(updateRequest.getTehsilId());
        if (updateRequest.getGender() != null) dbDriver.setGender(updateRequest.getGender());
        if (updateRequest.getAddress() != null) dbDriver.setAddress(updateRequest.getAddress());
        if (updateRequest.getDob() != null) dbDriver.setDob(updateRequest.getDob());

        if (updateRequest.getLicenseNo() != null) {
            if (!updateRequest.getLicenseNo().equals(dbDriver.getLicenseNo()) && driverRepository.existsByLicenseNo(updateRequest.getLicenseNo())) {
                publishFailureEvent(updateRequest, EventType.UPDATE, "License Number already exists");
                return;
            }
            dbDriver.setLicenseNo(updateRequest.getLicenseNo());
        }

        if (updateRequest.getLicenseExpiry() != null) dbDriver.setLicenseExpiry(updateRequest.getLicenseExpiry());

        try {
            driverRepository.save(dbDriver);
            profileRepository.save(dbUser);

            syncUserToRedis(dbUser, dbDriver, Role.DRIVER);

            publishSuccessEvent(updateRequest, EventType.UPDATE);
        } catch (Exception e) {
            publishFailureEvent(updateRequest, EventType.UPDATE, "Database Error: " + e.getMessage());
        }
    }

    @Override
    public DriverResponse getUserById(Map<String, Object> getRequest) {
        UUID userId = UUID.fromString((String) getRequest.get("userId"));
        String username = getRequest.get("username").toString();
        String role = getRequest.get("role").toString();

        UsersProfile dbUser = profileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found with User ID: " + userId));

        Driver dbDriver = driverRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver Not Found with User ID: " + userId));

        return ResponseConversion.toDriverResponse(username, role, dbUser, dbDriver);
    }

    // --- Redis Sync Helper  ---
    private void syncUserToRedis(UsersProfile profile, Driver driver, Role role) {
        String redisKey = "wtms:user:" + profile.getId();
        Map<String, Object> cacheData = new HashMap<>();

        cacheData.put("name", profile.getName());
        cacheData.put("email", profile.getEmail());
        cacheData.put("phoneNo", profile.getPhoneNo());
        cacheData.put("status", profile.getStatus() != null ? profile.getStatus().name() : "");
        cacheData.put("role", role.name());

        if (driver != null) {
            cacheData.put("fatherName", driver.getFatherName());
            cacheData.put("cnic", driver.getCnic());
            cacheData.put("gender", driver.getGender());
            cacheData.put("address", driver.getAddress());
            cacheData.put("licenseNo", driver.getLicenseNo());
            cacheData.put("dob", driver.getDob() != null ? driver.getDob().toString() : "");
            cacheData.put("licenseExpiry", driver.getLicenseExpiry() != null ? driver.getLicenseExpiry().toString() : "");
            cacheData.put("tehsilId", driver.getTehsilId() != null ? driver.getTehsilId().toString() : "");
        }

        redisTemplate.opsForHash().putAll(redisKey, cacheData);
    }

    // --- HELPER METHODS FOR SAGA EVENTS ---

    private void publishSuccessEvent(UserEventDto sourceData, EventType type) {

        UserEventDto driverData = UserEventDto.builder()
                .userId(sourceData.getUserId())
                .name(sourceData.getName())
                .fatherName(sourceData.getFatherName())
                .email(sourceData.getEmail())
                .dob(sourceData.getDob())
                .address(sourceData.getAddress())
                .cnic(sourceData.getCnic())
                .gender(sourceData.getGender())
                .phoneNo(sourceData.getPhoneNo())
                .licenseExpiry(sourceData.getLicenseExpiry())
                .licenseNo(sourceData.getLicenseNo())
                .role(Role.DRIVER)
                .status(Status.ACTIVE.name())
                .build();

        UserStatusEventDto eventDto = UserStatusEventDto.builder()
                .type(type)
                .eventTypeStatus(EventStatus.SUCCESS)
                .userData(driverData)
                .build();

        eventPublisher.publishEvent(eventDto);
    }

    private void publishFailureEvent(UserEventDto sourceData, EventType type, String reason) {

        System.err.println("Driver Event Failed: " + reason);

        UserEventDto payloadData = UserEventDto.builder()
                .userId(sourceData.getUserId())
                .status("BLOCK")
                .role(Role.DRIVER)
                .build();

        UserStatusEventDto eventDto = UserStatusEventDto.builder()
                .type(type)
                .eventTypeStatus(EventStatus.FAILURE)
                .userData(payloadData)
                .build();

        eventPublisher.publishEvent(eventDto);
    }
}