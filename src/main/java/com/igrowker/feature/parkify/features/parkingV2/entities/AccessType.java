package com.igrowker.feature.parkify.features.parkingV2.entities;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum AccessType {
    QR_CODE,
    PIN_CODE,
    MANUAL_CONTACT,
    OTHER;

    @JsonCreator
    public static AccessType fromString(String value) {
        for (AccessType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException(
            "Invalid value for AccessType: '" + value +
            "'. Valid values are: " + Arrays.toString(values()));
    }
}