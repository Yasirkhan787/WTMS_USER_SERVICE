package com.yasirkhan.user.producer;

import com.yasirkhan.user.models.dtos.UserStatusEventDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public UserEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserCreatedStatusEvent(UserStatusEventDto event) {
        try {
            kafkaTemplate.send("user-response-topic", event);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send status event", e);
        }
    }
}
