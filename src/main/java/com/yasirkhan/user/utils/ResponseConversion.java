package com.yasirkhan.user.utils;

import com.yasirkhan.user.models.entities.Driver;
import com.yasirkhan.user.models.entities.Role;
import com.yasirkhan.user.models.entities.Supervisor;
import com.yasirkhan.user.models.entities.UsersProfile;
import com.yasirkhan.user.responses.*;

public class ResponseConversion {

    public static AdminResponse toAdminResponse(
            String username,
            String role,
            UsersProfile savedUsersProfile){
        return
                AdminResponse
                        .builder()
                        .userID(savedUsersProfile.getId())
                        .username(username)
                        .email(savedUsersProfile.getEmail())
                        .role(Role.valueOf(role))
                        .name(savedUsersProfile.getName())
                        .phoneNo(savedUsersProfile.getPhoneNo())
                        .status(savedUsersProfile.getStatus())
                        .build();
    }

    public static SupervisorResponse toSupervisorResponse(
            String username,
            String role,
            UsersProfile savedUsersProfile,
            Supervisor savedSupervisor
    ){
        return
                SupervisorResponse
                        .builder()
                        .userID(savedUsersProfile.getId())
                        .username(username)
                        .email(savedUsersProfile.getEmail())
                        .role(role)
                        .name(savedUsersProfile.getName())
                        .fatherName(savedSupervisor.getFatherName())
                        .cnic(savedSupervisor.getCnic())
                        .gender(savedSupervisor.getGender())
                        .address(savedSupervisor.getAddress())
                        .phoneNo(savedUsersProfile.getPhoneNo())
                        .status(savedUsersProfile.getStatus().name())
                        .build();
    }

    public static DriverResponse toDriverResponse(
            String username,
            String role,
            UsersProfile savedUsersProfile,
            Driver savedDriver
    ){
        return
                DriverResponse
                        .builder()
                        .userID(savedUsersProfile.getId())
                        .username(username)
                        .email(savedUsersProfile.getEmail())
                        .role(role)
                        .name(savedUsersProfile.getName())
                        .fatherName(savedDriver.getFatherName())
                        .cnic(savedDriver.getCnic())
                        .gender(savedDriver.getGender())
                        .address(savedDriver.getAddress())
                        .phoneNo(savedUsersProfile.getPhoneNo())
                        .licenseNo(savedDriver.getLicenseNo())
                        .licenseExpiry(savedDriver.getLicenseExpiry())
                        .status(savedUsersProfile.getStatus().name())
                        .build();
    }

    public static UserResponse toUserResponse(UsersProfile profile) {
        UserResponse.UserResponseBuilder builder = UserResponse.builder()
                .id(profile.getId())
                .name(profile.getName())
                .email(profile.getEmail())
                .phoneNo(profile.getPhoneNo())
                .status(profile.getStatus().name());

        if (profile.getDriver() != null) {
            builder.role("DRIVER");
            builder.driverDetails(DriverDetails.builder()
                    .fatherName(profile.getDriver().getFatherName())
                    .cnic(profile.getDriver().getCnic())
                    .licenseNumber(profile.getDriver().getLicenseNo())
                    .licenseExpiry(profile.getDriver().getLicenseExpiry().toString())
                    .age(profile.getDriver().getAge())
                    .gender(profile.getDriver().getGender())
                    .address(profile.getDriver().getAddress())
                    .build());
        } else if (profile.getSupervisor() != null) {
            builder.role("SUPERVISOR");
            builder.supervisorDetails(SupervisorDetails.builder()
                    .fatherName(profile.getSupervisor().getFatherName())
                    .cnic(profile.getSupervisor().getCnic())
                    .gender(profile.getSupervisor().getGender())
                    .address(profile.getSupervisor().getAddress())
                    .build());
        } else {
            builder.role("ADMIN");
        }

        return builder.build();
    }
}