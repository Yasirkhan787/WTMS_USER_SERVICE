package com.yasirkhan.user.services.implementations;

import com.yasirkhan.user.models.entities.Status;
import com.yasirkhan.user.models.entities.UserProfile;
import com.yasirkhan.user.repositories.UserProfileRepository;
import com.yasirkhan.user.requests.UserRequest;
import com.yasirkhan.user.responses.AdminResponse;
import com.yasirkhan.user.services.AdminService;
import com.yasirkhan.user.utils.ResponseConversion;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserProfileRepository profileRepository;

    public AdminServiceImpl(UserProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }


    @Override
    public AdminResponse createAdmin(UserRequest request) {

        /*
            * TODO: Call to authService and get userID
            * Body {username, email, password, role, isBlocked}
            * Response [userId]
         */
        UUID userId = UUID.randomUUID();

        UserProfile adminUser = new UserProfile();

        adminUser.setId(userId);
        adminUser.setName(request.getName());
        adminUser.setEmail(request.getEmail());
        adminUser.setPhoneNo(request.getPhoneNo());
        adminUser.setStatus(request.getIsBlocked()?Status.ACTIVE:Status.BLOCK);

        UserProfile savedUser
                = profileRepository.save(adminUser);


        return
                ResponseConversion
                        .toAdminResponse(
                                request.getUsername(),
                                request.getRole().name(),
                                savedUser);
    }

    @Override
    public AdminResponse getUserById(UUID userID) {
        return null;
    }
}
