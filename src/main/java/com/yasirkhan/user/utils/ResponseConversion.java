package com.yasirkhan.user.utils;

import com.yasirkhan.user.models.entities.Driver;
import com.yasirkhan.user.models.entities.Supervisor;
import com.yasirkhan.user.models.entities.UsersProfile;
import com.yasirkhan.user.responses.AdminResponse;
import com.yasirkhan.user.responses.DriverResponse;
import com.yasirkhan.user.responses.SupervisorResponse;

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
                        .role(role)
                        .name(savedUsersProfile.getName())
                        .phoneNo(savedUsersProfile.getPhoneNo())
                        .status(savedUsersProfile.getStatus().name())
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
}
