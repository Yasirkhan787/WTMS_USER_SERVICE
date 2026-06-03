package com.yasirkhan.user.listeners;

import com.yasirkhan.user.models.dtos.UserStatusEventDto;
import com.yasirkhan.user.producer.UserEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserEventProducer producer;

    // Only fire SUCCESS if the DB actually committed the new profile
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, condition = "#eventDto.eventTypeStatus.name() == 'SUCCESS'")
    public void handleSuccessEvents(UserStatusEventDto eventDto) {
        producer.sendUserStatusEvent(eventDto);
        log.info("Saga SUCCESS Event published to Kafka for User ID: {}", eventDto.getUserData().getUserId());
    }

    // AFTER_COMPLETION guarantees this fires for both exceptions AND validation failures
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION, condition = "#eventDto.eventTypeStatus.name() == 'FAILURE'")
    public void handleFailureEvents(UserStatusEventDto eventDto) {
        producer.sendUserStatusEvent(eventDto);
        log.warn("Saga FAILURE Event published to Kafka for User ID: {}. Triggering Auth Rollback.", eventDto.getUserData().getUserId());
    }
}