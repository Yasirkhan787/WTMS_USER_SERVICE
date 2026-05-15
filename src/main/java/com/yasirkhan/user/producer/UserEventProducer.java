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

    public void sendUserCreatedStatusEvent(UserStatusEventDto event) {
        kafkaTemplate.send("user-response-topic", event).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("SUCCESS: User Updated event sent for ID: {} (Partition: {}, Offset: {})",
                        event.getUserId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("FAILED to send User Update event for ID: {}. Reason: {}",
                        event.getUserId(),
                        ex.getMessage());
            }
        });
    }
}

