package com.yasirkhan.user.controllers;

import com.yasirkhan.user.requests.UserRequest;
import com.yasirkhan.user.responses.UserResponse;
import com.yasirkhan.user.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
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

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR','DRIVER')")
    public ResponseEntity<?> getUserById(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Username") String username,
            @RequestHeader("X-User-Role") String role){

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
}
