package com.yasirkhan.user.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yasirkhan.user.models.enums.EventStatus;
import com.yasirkhan.user.models.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleResponseEventDto {

    private EventType type;
    private EventStatus eventTypeStatus;
    private UUID driverId;
    private String driverStatus;
}
