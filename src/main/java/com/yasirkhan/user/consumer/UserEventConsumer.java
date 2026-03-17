package com.yasirkhan.user.consumer;

import com.yasirkhan.user.exceptions.ResourceNotFoundException;
import com.yasirkhan.user.models.dtos.AuthUserResponseEvent;
import com.yasirkhan.user.models.entities.Status;
import com.yasirkhan.user.models.entities.UsersProfile;
import com.yasirkhan.user.repositories.UserProfileRepository;
import com.yasirkhan.user.services.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j // Recommended for logging rollbacks
public class UserEventConsumer {

    private final UserService userService;
    private final UserProfileRepository profileRepository;

    public UserEventConsumer(UserService userService, UserProfileRepository profileRepository) {
        this.userService = userService;
        this.profileRepository = profileRepository;
    }

    @KafkaListener(
            topics = "user-status-topic",
            groupId = "user-group",
            containerFactory = "listenerContainerFactory"
    )
    public void consumeUserStatusEvent(AuthUserResponseEvent eventDto){
        userService.updateUserStatus(eventDto.getUserId(), Status.valueOf(eventDto.getStatus()));
    }

    @KafkaListener(
            topics = "user-response-topic"
    )
    @Transactional
    public void handleAuthResponse(AuthUserResponseEvent event) {
        UUID userId = event.getUserId();

        if ("FAILURE".equals(event.getStatus())) {
            // Because of CascadeType.REMOVE in UsersProfile, deleting the profile
            // automatically deletes the associated Driver or Supervisor record.
            if (profileRepository.existsById(userId)) {
                profileRepository.deleteById(userId);
                log.info("Saga Rollback Success: Deleted profile and associated actor for ID: {}", userId);
            }
        } else {
            // Success logic: Activate the account
            UsersProfile profile = profileRepository.findById(userId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Profile not found for ID: " + userId));

            profile.setStatus(Status.ACTIVE);
            profileRepository.save(profile);
            log.info("Saga Transaction Finalized: User ID {} is now ACTIVE", userId);
        }
    }
}