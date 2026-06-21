package com.yasirkhan.user.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yasirkhan.user.models.enums.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TehsilResponseEventDto {

    private EventStatus eventTypeStatus;

    private UUID tehsilId;

    private String tehsilName;

    @JsonProperty("tehsilData")
    private void unpackNestedRouteData(Map<String, Object> tehsilData) {
        if (tehsilData != null) {
            this.tehsilId = UUID.fromString((String) tehsilData.get("tehsilId"));
            this.tehsilName = (String) tehsilData.get("tehsilName");
        }
    }
}
