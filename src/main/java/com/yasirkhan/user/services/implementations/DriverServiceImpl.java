package com.yasirkhan.user.services.implementations;

import com.yasirkhan.user.exceptions.DatabaseException;
import com.yasirkhan.user.exceptions.ResourceNotFoundException;
import com.yasirkhan.user.models.dtos.UserEventDto;
import com.yasirkhan.user.models.entities.Driver;
import com.yasirkhan.user.models.entities.Role;
import com.yasirkhan.user.models.entities.Status;
import com.yasirkhan.user.models.entities.UsersProfile;
import com.yasirkhan.user.producer.UserEventProducer;
import com.yasirkhan.user.repositories.DriverRepository;
import com.yasirkhan.user.repositories.UserProfileRepository;
import com.yasirkhan.user.requests.UserRequest;
import com.yasirkhan.user.responses.AuthUserResponse;
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
    public DriverResponse createDriver(UserRequest request) {

        UsersProfile driverProfile = new UsersProfile();

        driverProfile.setName(request.getName());
        driverProfile.setEmail(request.getEmail());
        driverProfile.setPhoneNo(request.getPhoneNo());
        driverProfile.setStatus(request.getIsBlocked()?Status.BLOCKED:Status.PENDING);

        UsersProfile savedDriverProfile
                = null;
        try {
            savedDriverProfile = profileRepository.save(driverProfile);
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        Driver driver =
                Driver
                        .builder()
                        .fatherName(request.getFatherName())
                        .cnic(request.getCnic())
                        .address(request.getAddress())
                        .gender(request.getGender())
                        .age(request.getAge())
                        .licenseNo(request.getLicenseNo())
                        .licenseExpiry(request.getLicenseExpiry())
                        .profile(savedDriverProfile)
                        .build();

        Driver savedDriver =
                null;
        try {
            savedDriver = driverRepository.save(driver);
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        // Send Event to Kafka
        UserEventDto eventDto =
                UserEventDto
                        .builder()
                        .userId(driverProfile.getId())
                        .username(request.getUsername())
                        .email(driverProfile.getEmail())
                        .password(request.getPassword())
                        .role(request.getRole())
                        .isBlocked(request.getIsBlocked())
                        .build();

        eventProducer.sendAuthUserCreateEvent(eventDto);

        return ResponseConversion.toDriverResponse(
                request.getUsername(),
                request.getRole().name(),
                savedDriverProfile,
                savedDriver
        );
    }

    @Override
    @Transactional
    public void updateDriver(Map<String, Object> updateRequest) {

        UUID userId = UUID.fromString(updateRequest.get("userId").toString());

        UsersProfile dbUser =
                profileRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "User Not Found with User ID: " + userId));

        Driver dbDriver =
                driverRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Driver Not Found with User ID: " + userId));

        UserEventDto eventDto =
                UserEventDto
                        .builder()
                        .userId(userId)
                        .build();

        // TODO: Use MapConstruct
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
                        case "fatherName" -> dbDriver.setFatherName((String) value);
                        case "cnic" -> dbDriver.setCnic((String) value);
                        case "gender" -> dbDriver.setGender((String) value);
                        case "phoneNo" -> dbUser.setPhoneNo((String) value);
                        case "address" -> dbDriver.setAddress((String) value);
                        case "age" -> dbDriver.setAge((int) value);
                        case "licenseNo" -> dbDriver.setLicenseNo((String) value);
                        case "licenseExpiry" -> dbDriver.setLicenseExpiry(LocalDate.parse((String) value));
                        case "status" -> dbUser.setStatus(Status.valueOf((String) value));
                    }
                }
        );

        driverRepository.save(dbDriver);

        if (eventDto.getEmail() != null || eventDto.getUsername() != null || eventDto.getRole() != null) {
            eventProducer.sendAuthUserUpdateEvent(eventDto);
        }
    }

    @Override
    public DriverResponse getUserById(Map<String, Object> getRequest) {

        UUID userId = UUID.fromString((String) getRequest.get("userId"));
        String username = getRequest.get("username").toString();
        String role = getRequest.get("role").toString();


        UsersProfile dbUser =
                profileRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "User Not Found with User ID: " + userId));

        Driver dbDriver =
                driverRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Driver Not Found with User ID: " + userId));
        return
                ResponseConversion
                        .toDriverResponse(
                                username,
                                role,
                                dbUser,
                                dbDriver
                        );
    }
}
