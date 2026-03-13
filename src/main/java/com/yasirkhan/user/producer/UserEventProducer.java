package com.yasirkhan.user.producer;

import com.yasirkhan.user.models.dtos.UserUpdateEventDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public UserEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserAuthUpdateEvent(UserUpdateEventDto updateEventDto){
        kafkaTemplate.send("user-update-topic", updateEventDto);
    }
}
