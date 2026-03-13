package com.yasirkhan.user.services.implementations;

import com.yasirkhan.user.models.entities.Status;
import com.yasirkhan.user.models.entities.Supervisor;
import com.yasirkhan.user.models.entities.UserProfile;
import com.yasirkhan.user.repositories.SupervisorRepository;
import com.yasirkhan.user.repositories.UserProfileRepository;
import com.yasirkhan.user.requests.UserRequest;
import com.yasirkhan.user.responses.SupervisorResponse;
import com.yasirkhan.user.services.SupervisorService;
import com.yasirkhan.user.utils.ResponseConversion;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SupervisorServiceImpl implements SupervisorService {

    private final UserProfileRepository profileRepository;

    private final SupervisorRepository supervisorRepository;

    public SupervisorServiceImpl(UserProfileRepository profileRepository, SupervisorRepository supervisorRepository) {
        this.profileRepository = profileRepository;
        this.supervisorRepository = supervisorRepository;
    }


    @Override
    public SupervisorResponse createSupervisor(UserRequest request) {

        /*
         * TODO: Call to authService and get userID
         * Body {username, email, password, role, isBlocked}
         * Response [userId]
         */
        UUID userId = UUID.randomUUID();

        UserProfile supervisorProfile = new UserProfile();

        supervisorProfile.setId(userId);
        supervisorProfile.setName(request.getName());
        supervisorProfile.setEmail(request.getEmail());
        supervisorProfile.setPhoneNo(request.getPhoneNo());
        supervisorProfile.setStatus(request.getIsBlocked()? Status.ACTIVE:Status.BLOCK);

        UserProfile savedUserProfile
                = profileRepository.save(supervisorProfile);

        Supervisor supervisor =
                Supervisor
                        .builder()
                        .fatherName(request.getFatherName())
                        .cnic(request.getCnic())
                        .address(request.getAddress())
                        .gender(request.getGender())
                        .profile(savedUserProfile)
                        .build();

        Supervisor savedSupervisor =
                supervisorRepository.save(supervisor);

        return
                ResponseConversion.toSupervisorResponse(
                        request.getUsername(),
                        request.getRole().name(),
                        savedUserProfile,
                        savedSupervisor);
    }

    @Override
    public SupervisorResponse getUserById(UUID userID) {
        return null;
    }
}
