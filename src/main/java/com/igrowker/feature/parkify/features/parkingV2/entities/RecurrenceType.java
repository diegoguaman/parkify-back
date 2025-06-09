package com.igrowker.feature.parkify.features.parkingV2.entities;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RecurrenceType {
    EVERY_DAY,
    MONDAY_TO_FRIDAY,
    SPECIFIC_DAYS;
    @JsonCreator
    public static RecurrenceType fromString(String value) {
        for (RecurrenceType tipo : values()) {
            if (tipo.name().equalsIgnoreCase(value)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException(
            "Invalid value for AccessType: '" + value +
            "'. Valid values are: " + Arrays.toString(values()));
    }
}