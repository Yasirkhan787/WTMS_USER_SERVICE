package com.yasirkhan.user.consumer;

import com.yasirkhan.user.models.dtos.AuthUserResponseEvent;
import com.yasirkhan.user.models.dtos.UserEventDto;
import com.yasirkhan.user.models.dtos.UserStatusEventDto;
import com.yasirkhan.user.models.entities.Status;
import com.yasirkhan.user.repositories.UserProfileRepository;
import com.yasirkhan.user.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

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
            topics = "user-created-topic",
            groupId = "user-group",
            containerFactory = "listenerContainerFactory"
    )
    public void consumeUserCreationEvent(UserEventDto eventDto){
        userService.addUser(eventDto);
    }

    @KafkaListener(
            topics = "user-updated-topic",
            groupId = "user-group",
            containerFactory = "listenerContainerFactory"
    )
    public void consumeUserUpdationEvent(UserEventDto eventDto){
        userService.updateUser(eventDto);
    }


    @KafkaListener(
            topics = "user-status-topic",
            groupId = "user-group",
            containerFactory = "listenerContainerFactory"
    )
    public void consumeUserStatusEvent(UserStatusEventDto eventDto){
        userService.updateUserStatus(eventDto.getUserId(), Status.valueOf(eventDto.getStatus()));
    }
}