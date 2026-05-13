package com.yasirkhan.user.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yasirkhan.user.models.dtos.UserEventDto;
import com.yasirkhan.user.models.dtos.UserStatusEventDto;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

public class CustomDeserializer implements Deserializer<Object> {

    private final ObjectMapper objectMapper;

    public CustomDeserializer() {
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    @Override
    public Object deserialize(String topic, byte[] data) {
        try {
            if (data == null) return null;

            // ROUTE BASED ON TOPIC NAME
            if ("user-status-topic".equals(topic)) {
                return objectMapper.readValue(data, UserStatusEventDto.class);
            }
            else if ("user-created-topic".equals(topic) || "user-updated-topic".equals(topic)) {
                return objectMapper.readValue(data, UserEventDto.class);
            }

            throw new SerializationException("Unknown topic for deserialization: " + topic);

        } catch (Exception e) {
            throw new SerializationException("Error deserializing message from topic: " + topic, e);
        }
    }
}