package com.yasirkhan.user.producer;

import com.yasirkhan.user.models.dtos.UserStatusEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public UserEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserStatusEvent(UserStatusEventDto event) {
        kafkaTemplate.send("user-response-topic", event).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("SUCCESS: User {} event sent for ID: {} (Partition: {}, Offset: {})",
                        event.getType(),
                        event.getUserData().getUserId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("FAILED to send User Update event for ID: {}. Reason: {}",
                        event.getUserData().getUserId(),
                        ex.getMessage());
            }
        });
    }
}

