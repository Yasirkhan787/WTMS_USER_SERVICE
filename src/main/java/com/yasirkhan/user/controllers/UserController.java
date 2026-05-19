package com.yasirkhan.user.controllers;

import com.yasirkhan.user.responses.UserResponse;
import com.yasirkhan.user.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR','DRIVER')")
    public ResponseEntity<?> getUserById(
            @RequestAttribute("userId") String userId,
            @RequestAttribute("username") String username,
            @RequestAttribute("role") String role) {

        return
                ResponseEntity.ok(userService.getUserById(username, userId, role));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUser(){
        return
                ResponseEntity.ok(userService.getAllUser());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id){
        return
                ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/admins")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllAdmins(){
        return
                ResponseEntity.ok(userService.getAllAdmins());
    }

    @GetMapping("/supervisors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllSupervisors(){
        return
                ResponseEntity.ok(userService.getAllSupervisors());
    }

    @GetMapping("/drivers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllDrivers(){
        return
                ResponseEntity.ok(userService.getAllDrivers());
    }
}


