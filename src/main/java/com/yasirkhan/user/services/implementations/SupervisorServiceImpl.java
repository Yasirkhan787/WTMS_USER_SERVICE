package com.yasirkhan.user.services.implementations;

import com.yasirkhan.user.models.dtos.UserUpdateEventDto;
import com.yasirkhan.user.models.entities.Role;
import com.yasirkhan.user.models.entities.Status;
import com.yasirkhan.user.models.entities.Supervisor;
import com.yasirkhan.user.models.entities.UsersProfile;
import com.yasirkhan.user.producer.UserEventProducer;
import com.yasirkhan.user.repositories.SupervisorRepository;
import com.yasirkhan.user.repositories.UserProfileRepository;
import com.yasirkhan.user.requests.UserRequest;
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

    public SupervisorServiceImpl(UserProfileRepository profileRepository, SupervisorRepository supervisorRepository, UserEventProducer eventProducer) {
        this.profileRepository = profileRepository;
        this.supervisorRepository = supervisorRepository;
        this.eventProducer = eventProducer;
    }


    @Override
    @Transactional
    public SupervisorResponse createSupervisor(UserRequest request) {

        /*
         * TODO: Call to authService and get userID
         * Body {username, email, password, role, isBlocked}
         * Response [userId]
         */
        UUID userId = UUID.randomUUID();

        UsersProfile supervisorProfile = new UsersProfile();

        supervisorProfile.setId(userId);
        supervisorProfile.setName(request.getName());
        supervisorProfile.setEmail(request.getEmail());
        supervisorProfile.setPhoneNo(request.getPhoneNo());
        supervisorProfile.setStatus(request.getIsBlocked()? Status.ACTIVE:Status.BLOCK);

        UsersProfile savedUsersProfile
                = profileRepository.save(supervisorProfile);

        Supervisor supervisor =
                Supervisor
                        .builder()
                        .fatherName(request.getFatherName())
                        .cnic(request.getCnic())
                        .address(request.getAddress())
                        .gender(request.getGender())
                        .profile(savedUsersProfile)
                        .build();

        Supervisor savedSupervisor =
                supervisorRepository.save(supervisor);

        return
                ResponseConversion.toSupervisorResponse(
                        request.getUsername(),
                        request.getRole().name(),
                        savedUsersProfile,
                        savedSupervisor);
    }

    // TODO: IF username update send kafka event
    @Override
    @Transactional
    public void updateSupervisor(Map<String, Object> updateRequest) {

        UUID userId = UUID.fromString((String) updateRequest.get("userId"));

        UsersProfile dbUser =
                profileRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "User Not Found with User ID: " + userId));

        Supervisor dbSupervisor =
                supervisorRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Supervisor Not Found with User ID: " + userId));

        UserUpdateEventDto eventDto =
                UserUpdateEventDto
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
                            eventDto.setRole((Role) value);
                        }
                        case "name" -> dbUser.setName((String) value);
                        case "fatherName" -> dbSupervisor.setFatherName((String) value);
                        case "cnic" -> dbSupervisor.setCnic((String) value);
                        case "gender" -> dbSupervisor.setGender((String) value);
                        case "phoneNo" -> dbUser.setPhoneNo((String) value);
                        case "address" -> dbSupervisor.setAddress((String) value);
                       case "status" -> dbUser.setStatus(Status.valueOf((String) value));
                    }
                }
        );

        supervisorRepository.save(dbSupervisor);

        if (eventDto.getEmail() != null || eventDto.getUsername() != null || eventDto.getRole() != null) {
            eventProducer.sendUserAuthUpdateEvent(eventDto);
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
                                () -> new RuntimeException(
                                        "User Not Found with User ID: " + userId));

        Supervisor dbSupervisor =
                supervisorRepository
                        .findById(userId)
                        .orElseThrow(
                                () -> new RuntimeException(
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
