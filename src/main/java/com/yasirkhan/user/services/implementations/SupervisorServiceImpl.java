package com.yasirkhan.user.services.implementations;

import com.yasirkhan.user.exceptions.DatabaseException;
import com.yasirkhan.user.exceptions.ResourceNotFoundException;
import com.yasirkhan.user.models.dtos.UserEventDto;
import com.yasirkhan.user.models.entities.Role;
import com.yasirkhan.user.models.entities.Status;
import com.yasirkhan.user.models.entities.Supervisor;
import com.yasirkhan.user.models.entities.UsersProfile;
import com.yasirkhan.user.producer.UserEventProducer;
import com.yasirkhan.user.repositories.SupervisorRepository;
import com.yasirkhan.user.repositories.UserProfileRepository;
import com.yasirkhan.user.requests.UserRequest;
import com.yasirkhan.user.responses.AuthUserResponse;
import com.yasirkhan.user.responses.SupervisorResponse;
import com.yasirkhan.user.services.SupervisorService;
import com.yasirkhan.user.utils.ResponseConversion;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class SupervisorServiceImpl implements SupervisorService {

    private final UserProfileRepository profileRepository;

    private final SupervisorRepository supervisorRepository;

    private final UserEventProducer eventProducer;

    public SupervisorServiceImpl(UserProfileRepository profileRepository, SupervisorRepository supervisorRepository,
                                 UserEventProducer eventProducer) {

        this.profileRepository = profileRepository;
        this.supervisorRepository = supervisorRepository;
        this.eventProducer = eventProducer;
    }

    @Override
    @Transactional
    public SupervisorResponse createSupervisor(UserRequest request) {

        UsersProfile supervisorProfile =
                new UsersProfile();

        supervisorProfile.setName(request.getName());
        supervisorProfile.setEmail(request.getEmail());
        supervisorProfile.setPhoneNo(request.getPhoneNo());
        supervisorProfile.setStatus(request.getIsBlocked()?Status.BLOCKED:Status.PENDING);

        UsersProfile savedUsersProfile
                = null;
        try {
            savedUsersProfile = profileRepository.save(supervisorProfile);
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        Supervisor supervisor =
                Supervisor
                        .builder()
                        .fatherName(request.getFatherName())
                        .cnic(request.getCnic())
                        .address(request.getAddress())
                        .gender(request.getGender())
                        .age(request.getAge()) // change to DOB
                        .profile(savedUsersProfile)
                        .build();

        Supervisor savedSupervisor =
                null;
        try {
            savedSupervisor = supervisorRepository.save(supervisor);
        } catch (Exception e) {
            throw new DatabaseException(e.getMessage());
        }

        // Send Event to Kafka
        UserEventDto eventDto =
                UserEventDto
                        .builder()
                        .userId(supervisorProfile.getId())
                        .username(request.getUsername())
                        .email(supervisorProfile.getEmail())
                        .password(request.getPassword())
                        .role(request.getRole())
                        .isBlocked(request.getIsBlocked())
                        .build();

        eventProducer.sendAuthUserCreateEvent(eventDto);

        return
                ResponseConversion.toSupervisorResponse(
                        request.getUsername(),
                        request.getRole().name(),
                        savedUsersProfile,
                        savedSupervisor);
    }

    @Override
    @Transactional
    public void updateSupervisor(Map<String, Object> updateRequest) {

        UUID userId = UUID.fromString(updateRequest.get("userId").toString());

        UsersProfile dbUser =
                profileRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "User Not Found with User ID: " + userId));

        Supervisor dbSupervisor =
                supervisorRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Supervisor Not Found with User ID: " + userId));

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
                        case "fatherName" -> dbSupervisor.setFatherName((String) value);
                        case "cnic" -> dbSupervisor.setCnic((String) value);
                        case "gender" -> dbSupervisor.setGender((String) value);
                        case "phoneNo" -> dbUser.setPhoneNo((String) value);
                        case "address" -> dbSupervisor.setAddress((String) value);
                        case "age" -> dbSupervisor.setAge((int) value);
                        case "status" -> dbUser.setStatus(Status.valueOf((String) value));
                    }
                }
        );

        supervisorRepository.save(dbSupervisor);

        if (eventDto.getEmail() != null || eventDto.getUsername() != null || eventDto.getRole() != null) {
            eventProducer.sendAuthUserUpdateEvent(eventDto);
        }
    }

    @Override
    public SupervisorResponse getUserById(Map<String, Object> getRequest) {

        UUID userId = UUID.fromString((String) getRequest.get("userId"));
        String username = getRequest.get("username").toString();
        String role = getRequest.get("role").toString();

        UsersProfile dbUser =
                profileRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "User Not Found with User ID: " + userId));

        Supervisor dbSupervisor =
                supervisorRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Supervisor Not Found with User ID: " + userId));
        return
                ResponseConversion
                        .toSupervisorResponse(
                                username,
                                role,
                                dbUser,
                                dbSupervisor
                        );
    }
}
