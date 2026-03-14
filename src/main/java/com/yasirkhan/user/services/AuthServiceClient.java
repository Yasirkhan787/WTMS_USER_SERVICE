package com.yasirkhan.user.services;

import com.yasirkhan.user.requests.AuthUserRequest;
import com.yasirkhan.user.responses.AuthUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service", path = "/auth/user")
public interface AuthServiceClient {

    @PostMapping("/add")
    ResponseEntity<AuthUserResponse> createAuthUser(@RequestBody AuthUserRequest authRequest);
}
