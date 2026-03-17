package com.yasirkhan.user.producer;

import com.yasirkhan.user.models.dtos.UserEventDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public UserEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAuthUserCreateEvent(UserEventDto updateEventDto){
        kafkaTemplate.send("user-created-topic", updateEventDto);
    }

    public void sendAuthUserUpdateEvent(UserEventDto updateEventDto){
        kafkaTemplate.send("user-updated-topic", updateEventDto);
    }
}
