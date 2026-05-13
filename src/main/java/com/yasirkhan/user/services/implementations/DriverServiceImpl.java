package com.yasirkhan.user.services.implementations;

import com.yasirkhan.user.exceptions.DatabaseException;
import com.yasirkhan.user.exceptions.ResourceNotFoundException;
import com.yasirkhan.user.models.dtos.UserEventDto;
import com.yasirkhan.user.models.dtos.UserStatusEventDto;
import com.yasirkhan.user.models.entities.Driver;
import com.yasirkhan.user.models.entities.Role;
import com.yasirkhan.user.models.entities.Status;
import com.yasirkhan.user.models.entities.UsersProfile;
import com.yasirkhan.user.producer.UserEventProducer;
import com.yasirkhan.user.repositories.DriverRepository;
import com.yasirkhan.user.repositories.UserProfileRepository;
import com.yasirkhan.user.responses.DriverResponse;
import com.yasirkhan.user.services.DriverService;
import com.yasirkhan.user.utils.ResponseConversion;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Service
public class DriverServiceImpl implements DriverService {

    private final UserProfileRepository profileRepository;
    private final DriverRepository driverRepository;
    private final UserEventProducer eventProducer;

    public DriverServiceImpl(UserProfileRepository profileRepository, DriverRepository driverRepository, UserEventProducer eventProducer) {
        this.profileRepository = profileRepository;
        this.driverRepository = driverRepository;
        this.eventProducer = eventProducer;
    }

    @Override
    @Transactional
    public void createDriver(UserEventDto request) {

        UsersProfile driverProfile = new UsersProfile();

        driverProfile.setId(request.getUserId());
        driverProfile.setName(request.getName());
        driverProfile.setEmail(request.getEmail());
        driverProfile.setPhoneNo(request.getPhoneNo());
        driverProfile.setStatus(Status.ACTIVE);

        try {

            profileRepository.saveAndFlush(driverProfile);

            Driver driver = Driver.builder()
                    .fatherName(request.getFatherName())
                    .cnic(request.getCnic())
                    .address(request.getAddress())
                    .gender(request.getGender())
                    .age(request.getAge()) // change to DOB
                    .licenseNo(request.getLicenseNo())
                    .licenseExpiry(request.getLicenseExpiry())
                    .profile(driverProfile)
                    .build();

            driverRepository.saveAndFlush(driver);

            UserStatusEventDto successEvent = UserStatusEventDto.builder()
                    .userId(request.getUserId())
                    .status("SUCCESS")
                    .build();

            eventProducer.sendUserCreatedStatusEvent(successEvent);

        } catch (Exception e) {

            UserStatusEventDto failureEvent = UserStatusEventDto.builder()
                    .userId(request.getUserId())
                    .status("FAILURE")
                    .build();
            eventProducer.sendUserCreatedStatusEvent(failureEvent);

            throw new DatabaseException("Failed to save Driver. Initiated Rollback. Error: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateDriver(UserEventDto updateRequest) {

        UUID userId = updateRequest.getUserId();

        UsersProfile dbUser = profileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found with User ID: " + userId));

        Driver dbDriver = driverRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver Not Found with User ID: " + userId));

        if (updateRequest.getEmail() != null) dbUser.setEmail(updateRequest.getEmail());
        if (updateRequest.getName() != null) dbUser.setName(updateRequest.getName());
        if (updateRequest.getPhoneNo() != null) dbUser.setPhoneNo(updateRequest.getPhoneNo());
        if (updateRequest.getStatus() != null) dbUser.setStatus(Status.valueOf(updateRequest.getStatus()));

        if (updateRequest.getFatherName() != null) dbDriver.setFatherName(updateRequest.getFatherName());
        if (updateRequest.getCnic() != null) dbDriver.setCnic(updateRequest.getCnic());
        if (updateRequest.getGender() != null) dbDriver.setGender(updateRequest.getGender());
        if (updateRequest.getAddress() != null) dbDriver.setAddress(updateRequest.getAddress());
        if (updateRequest.getAge() != null) dbDriver.setAge(updateRequest.getAge());
        if (updateRequest.getLicenseNo() != null) dbDriver.setLicenseNo(updateRequest.getLicenseNo());
        if (updateRequest.getLicenseExpiry() != null) dbDriver.setLicenseExpiry(updateRequest.getLicenseExpiry());

        driverRepository.save(dbDriver);
        profileRepository.save(dbUser);
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
}