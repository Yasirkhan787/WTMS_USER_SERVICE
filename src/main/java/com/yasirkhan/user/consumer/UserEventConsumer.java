package com.yasirkhan.user.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventConsumer {

    @KafkaListener(
            topics = "user-status-topic",
            groupId = "user-group",
            containerFactory = "listenerContainerFactory"
    )
    public void consumeUserStatusEvent( ){

    }
}
