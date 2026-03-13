package com.yasirkhan.user.controllers;

import com.yasirkhan.user.requests.UserRequest;
import com.yasirkhan.user.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addUser(@RequestBody UserRequest request){

        return
                ResponseEntity.ok(userService.addUser(request));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUser(){
        return null;
    }

    // Get User By ID
    @GetMapping
    public ResponseEntity<?> getUserById(
            @RequestHeader String userId,
            @RequestHeader String role){

        return
                ResponseEntity.ok(userService.getUserById(userId, role));

    }















//    @GetMapping("/all")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<List<UserResponse>> getAll(){
//
//        return
//                ResponseEntity.ok(userService.getAllUser());
//    }
//
//    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id){
//
//        return
//                ResponseEntity.ok(userService.getUserById(id));
//    }
//
//    @PutMapping("/update/{id}")
////    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
//    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id, @RequestBody UserRequest updateRequest){
//
//        return
//                ResponseEntity.ok(userService.updateUser(id, updateRequest));
//    }
//
//    @PutMapping("/block/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<String> blockUser(@PathVariable UUID id, @RequestParam Boolean blockStatus ){
//
//        driverService.toggleDriverStatus(id, blockStatus);
//
//        return new ResponseEntity<>("User with ID:" + id + "Blocked Successfully", HttpStatus.NO_CONTENT);
//    }
}
