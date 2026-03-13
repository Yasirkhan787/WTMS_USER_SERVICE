package com.yasirkhan.user.services.implementations;

import com.yasirkhan.user.models.entities.Driver;
import com.yasirkhan.user.models.entities.Status;
import com.yasirkhan.user.models.entities.UserProfile;
import com.yasirkhan.user.repositories.DriverRepository;
import com.yasirkhan.user.repositories.UserProfileRepository;
import com.yasirkhan.user.requests.UserRequest;
import com.yasirkhan.user.responses.DriverResponse;
import com.yasirkhan.user.services.DriverService;
import com.yasirkhan.user.utils.ResponseConversion;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DriverServiceImpl implements DriverService {

    private final UserProfileRepository profileRepository;

    private final DriverRepository driverRepository;

    public DriverServiceImpl(UserProfileRepository profileRepository, DriverRepository driverRepository) {
        this.profileRepository = profileRepository;
        this.driverRepository = driverRepository;
    }

    @Override
    public DriverResponse createDriver(UserRequest request) {

        /*
         * TODO: Call to authService and get userID
         * Body {username, email, password, role, isBlocked}
         * Response [userId]
         */
        UUID userId = UUID.randomUUID();

        UserProfile driverProfile = new UserProfile();

        driverProfile.setId(userId);
        driverProfile.setName(request.getName());
        driverProfile.setEmail(request.getEmail());
        driverProfile.setPhoneNo(request.getPhoneNo());
        driverProfile.setStatus(request.getIsBlocked()?Status.ACTIVE:Status.BLOCK);
        UserProfile savedUserProfile
                = profileRepository.save(driverProfile);

        Driver driver =
                Driver
                        .builder()
                        .fatherName(request.getFatherName())
                        .cnic(request.getCnic())
                        .address(request.getAddress())
                        .licenseExpiry(request.getLicenseExpiry())
                        .licenseNo(request.getLicenseNo())
                        .profile(savedUserProfile)
                        .build();

        Driver savedDriver =
                driverRepository.save(driver);

        return
                ResponseConversion.toDriverResponse(
                        request.getUsername(),
                        request.getRole().name(),
                        savedUserProfile,
                        savedDriver);
    }

    @Override
    public DriverResponse getUserById(UUID userID) {
        return null;
    }

}
