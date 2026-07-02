package com.yasirkhan.user.consumer;

import com.yasirkhan.user.models.dtos.UserEventDto;
import com.yasirkhan.user.models.dtos.UserStatusEventDto;
import com.yasirkhan.user.models.enums.EventType;
import com.yasirkhan.user.models.enums.Status;
import com.yasirkhan.user.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserEventConsumer {

    private final UserService userService;

    public UserEventConsumer(UserService userService) {
        this.userService = userService;
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
    public void consumeUserStatusEvent(UserStatusEventDto eventDto) {
        if (EventType.BLOCK.equals(eventDto.getType())) {
            userService.updateUserStatus(eventDto.getUserData().getUserId(), Status.valueOf(eventDto.getUserData().getStatus()));
        }
    }
}