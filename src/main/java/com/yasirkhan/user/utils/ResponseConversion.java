package com.yasirkhan.user.utils;

import com.yasirkhan.user.models.entities.Driver;
import com.yasirkhan.user.models.entities.Supervisor;
import com.yasirkhan.user.models.entities.UserProfile;
import com.yasirkhan.user.responses.AdminResponse;
import com.yasirkhan.user.responses.DriverResponse;
import com.yasirkhan.user.responses.SupervisorResponse;

public class ResponseConversion {

    public static AdminResponse toAdminResponse(
            String username,
            String role,
            UserProfile savedUserProfile){
        return
                AdminResponse
                        .builder()
                        .userID(savedUserProfile.getId())
                        .username(username)
                        .email(savedUserProfile.getEmail())
                        .role(role)
                        .name(savedUserProfile.getName())
                        .phoneNo(savedUserProfile.getPhoneNo())
                        .status(savedUserProfile.getStatus().name())
                        .build();
    }

    public static SupervisorResponse toSupervisorResponse(
            String username,
            String role,
            UserProfile savedUserProfile,
            Supervisor savedSupervisor
    ){
        return
                SupervisorResponse
                        .builder()
                        .userID(savedUserProfile.getId())
                        .username(username)
                        .email(savedUserProfile.getEmail())
                        .role(role)
                        .name(savedUserProfile.getName())
                        .fatherName(savedSupervisor.getFatherName())
                        .cnic(savedSupervisor.getCnic())
                        .gender(savedSupervisor.getGender())
                        .address(savedSupervisor.getAddress())
                        .phoneNo(savedUserProfile.getPhoneNo())
                        .status(savedUserProfile.getStatus().name())
                        .build();
    }

    public static DriverResponse toDriverResponse(
            String username,
            String role,
            UserProfile savedUserProfile,
            Driver savedDriver
    ){
        return
                DriverResponse
                        .builder()
                        .userID(savedUserProfile.getId())
                        .username(username)
                        .email(savedUserProfile.getEmail())
                        .role(role)
                        .name(savedUserProfile.getName())
                        .fatherName(savedDriver.getFatherName())
                        .cnic(savedDriver.getCnic())
                        .gender(savedDriver.getGender())
                        .address(savedDriver.getAddress())
                        .phoneNo(savedUserProfile.getPhoneNo())
                        .licenseNo(savedDriver.getLicenseNo())
                        .licenseExpiry(savedDriver.getLicenseExpiry())
                        .status(savedUserProfile.getStatus().name())
                        .build();
    }
}
