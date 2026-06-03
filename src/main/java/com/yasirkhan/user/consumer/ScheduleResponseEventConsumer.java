package com.yasirkhan.user.consumer;

import com.yasirkhan.user.models.dtos.ScheduleResponseEventDto;
import com.yasirkhan.user.models.enums.EventStatus;
import com.yasirkhan.user.models.enums.Status;
import com.yasirkhan.user.repositories.UserProfileRepository;
import com.yasirkhan.user.services.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ScheduleResponseEventConsumer {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserService userService;

    public ScheduleResponseEventConsumer(RedisTemplate<String, Object> redisTemplate, UserService userService) {
        this.redisTemplate = redisTemplate;
        this.userService = userService;
    }

    @KafkaListener(
            topics = "schedule-response-topic",
            groupId = "user-group",
            containerFactory = "listenerContainerFactory"
    )
    public void handleScheduleResponse(ScheduleResponseEventDto event) {

        if (EventStatus.SUCCESS.equals(event.getEventTypeStatus())) {

            // Update the primary PostgreSQL Database
            userService.updateUserStatus(event.getDriverId(), Status.valueOf(event.getDriverStatus()));

            // Update the fast Redis Cache
            String redisUserKey = "wtms:user:" + event.getDriverId();
            redisTemplate.opsForHash().put(redisUserKey, "status", event.getDriverStatus());
        }
    }
}