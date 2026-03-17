package com.yasirkhan.user.services.implementations;

import com.yasirkhan.user.requests.AuthUserRequest;
import com.yasirkhan.user.requests.UserRequest;
import com.yasirkhan.user.responses.AuthUserResponse;
import com.yasirkhan.user.services.AuthServiceClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthClientService {

    private final AuthServiceClient authClient;

    public AuthClientService(AuthServiceClient authClient) {
        this.authClient = authClient;
    }

    public AuthUserResponse createAuthUser(UserRequest userRequest) {

        AuthUserRequest request =
                AuthUserRequest
                        .builder()
                        .username(userRequest.getUsername())
                        .email(userRequest.getEmail())
                        .password(userRequest.getPassword())
                        .role(userRequest.getRole())
                        .isBlocked(userRequest.getIsBlocked())
                        .build();

        ResponseEntity<AuthUserResponse> authResponse
                = authClient.createAuthUser(request);

        if (authResponse.getBody() == null || !authResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Credential creation failed in Auth-Service");
        }
        // Extract the UUID returned by Auth-Service
        AuthUserResponse createdAuthUser =
                authResponse
                        .getBody();

        return createdAuthUser;
    }
}
