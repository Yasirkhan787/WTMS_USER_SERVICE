package com.yasirkhan.user.services.implementations;

import com.yasirkhan.user.exceptions.DatabaseException;
import com.yasirkhan.user.exceptions.ResourceNotFoundException;
import com.yasirkhan.user.models.dtos.UserEventDto;
import com.yasirkhan.user.models.dtos.UserStatusEventDto;
import com.yasirkhan.user.models.entities.Role;
import com.yasirkhan.user.models.entities.Status;
import com.yasirkhan.user.models.entities.Supervisor;
import com.yasirkhan.user.models.entities.UsersProfile;
import com.yasirkhan.user.producer.UserEventProducer;
import com.yasirkhan.user.repositories.SupervisorRepository;
import com.yasirkhan.user.repositories.UserProfileRepository;
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
    public void createSupervisor(UserEventDto request) {

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
                    .profile(supervisorProfile)
                    .build();

            supervisorRepository.saveAndFlush(supervisor);

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

            throw new DatabaseException("Failed to save Supervisor. Initiated Rollback. Error: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void updateSupervisor(UserEventDto updateRequest) {

        UUID userId = updateRequest.getUserId();

        UsersProfile dbUser = profileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found with User ID: " + userId));

        Supervisor dbSupervisor = supervisorRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Supervisor Not Found with User ID: " + userId));

        if (updateRequest.getEmail() != null) dbUser.setEmail(updateRequest.getEmail());
        if (updateRequest.getName() != null) dbUser.setName(updateRequest.getName());
        if (updateRequest.getPhoneNo() != null) dbUser.setPhoneNo(updateRequest.getPhoneNo());
        if (updateRequest.getStatus() != null) dbUser.setStatus(Status.valueOf(updateRequest.getStatus()));

        if (updateRequest.getFatherName() != null) dbSupervisor.setFatherName(updateRequest.getFatherName());
        if (updateRequest.getCnic() != null) dbSupervisor.setCnic(updateRequest.getCnic());
        if (updateRequest.getGender() != null) dbSupervisor.setGender(updateRequest.getGender());
        if (updateRequest.getAddress() != null) dbSupervisor.setAddress(updateRequest.getAddress());
        if (updateRequest.getDob() != null) dbSupervisor.setDob(updateRequest.getDob());

        supervisorRepository.save(dbSupervisor);
        profileRepository.save(dbUser);
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
}