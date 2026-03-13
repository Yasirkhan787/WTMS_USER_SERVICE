package com.yasirkhan.user.controllers;

import com.yasirkhan.user.requests.UserRequest;
import com.yasirkhan.user.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addUser(@RequestBody UserRequest request){

        return
                ResponseEntity.ok(userService.addUser(request));
    }

    /*
        * Body: Must Add userId and role in Request Body
     */
    @PatchMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateUser(
        @RequestBody Map<String, Object> updateRequest){

        userService.updateUser(updateRequest);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUser(){
        return null;
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','SUPERVISOR','DRIVER')")
    public ResponseEntity<?> getUserById(
            @RequestHeader String userId,
            @RequestHeader String username,
            @RequestHeader String role){

        return
                ResponseEntity.ok(userService.getUserById(username, userId, role));
    }
}
