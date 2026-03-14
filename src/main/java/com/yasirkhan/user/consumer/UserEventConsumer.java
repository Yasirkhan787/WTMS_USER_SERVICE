package com.yasirkhan.user.consumer;

import com.yasirkhan.user.models.dtos.UserStatusUpdateEventDto;
import com.yasirkhan.user.models.entities.Status;
import com.yasirkhan.user.services.UserService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventConsumer {

    private final UserService userService;

    public UserEventConsumer(UserService userService) {
        this.userService = userService;
    }

    @KafkaListener(
            topics = "user-status-topic",
            groupId = "user-group",
            containerFactory = "listenerContainerFactory"
    )
    public void consumeUserStatusEvent(UserStatusUpdateEventDto eventDto){
        userService.updateUserStatus(eventDto.getId(), Status.valueOf(eventDto.getUserStatus()));
    }
}
