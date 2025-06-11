package com.igrowker.feature.parkify.features.parkingV2.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.igrowker.feature.parkify.features.parkingV2.dto.request.ShiftRequestDTO;
import com.igrowker.feature.parkify.features.parkingV2.entities.RecurrenceType;
import com.igrowker.feature.parkify.features.parkingV2.entities.Shift;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ShiftValidator {

    public List<String> validateBaseRules(ShiftRequestDTO dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getStartTime().equals(dto.getEndTime())) {
            errors.add("startTime and endTime must not be equal.");
        }

        if (dto.getEndTime().isBefore(dto.getStartTime()) && !dto.isOvernight()) {
            errors.add("endTime cannot be earlier than startTime unless the shift is marked as overnight.");
        }

        if (dto.isOvernight() && !dto.getEndTime().isBefore(dto.getStartTime())) {
            errors.add("Shift is marked as overnight, but endTime is not earlier than startTime.");
        }

        if (dto.getRecurrenceType() == RecurrenceType.SPECIFIC_DAYS) {
            if (dto.getSpecificDays() == null || dto.getSpecificDays().isEmpty()) {
                errors.add("If recurrenceType is 'SPECIFIC_DAYS', you must provide at least one day.");
            } else if (!dto.getSpecificDays().stream().allMatch(d -> d >= 0 && d <= 6)) {
                errors.add("Specific days must be between 0 (Sunday) and 6 (Saturday).");
            }
        }

        return errors;
    }

   public List<String> validateNoOverlap(ShiftRequestDTO dto, List<Shift> existingShifts) {
        List<String> errors = new ArrayList<>();
        List<Interval> newIntervals = splitIntoIntervals(dto.getStartTime(), dto.getEndTime());
        Set<Integer> newDays = new HashSet<>(resolveDays(dto.getRecurrenceType(), dto.getSpecificDays()));

        for (Shift existing : existingShifts) {
            Set<Integer> existingDays = new HashSet<>(resolveDays(existing.getRecurrenceType(), existing.getSpecificDays()));

            Set<Integer> overlappingDays = new HashSet<>(newDays);
            overlappingDays.retainAll(existingDays);

            if (!overlappingDays.isEmpty()) {
                List<Interval> existingIntervals = splitIntoIntervals(existing.getStartTime(), existing.getEndTime());

                for (Interval newInterval : newIntervals) {
                    for (Interval existingInterval : existingIntervals) {
                        if (newInterval.overlapsWith(existingInterval)) {
                            String daysStr = overlappingDays.stream()
                                    .map(this::dayNameFromIndex)
                                    .collect(Collectors.joining(", "));

                            errors.add("The shift overlaps with an existing one: " +
                                    (existing.getName() != null ? existing.getName() : "unnamed") +
                                    " (" + existing.getStartTime() + " - " + existing.getEndTime() + ") " +
                                    "on the following day(s): " + daysStr);
                            return errors;
                        }
                    }
                }
            }
        }

        return errors;
    }

    
    
    private String dayNameFromIndex(int dayIndex) {
        return switch (dayIndex) {
            case 0 -> "Sunday";
            case 1 -> "Monday";
            case 2 -> "Tuesday";
            case 3 -> "Wednesday";
            case 4 -> "Thursday";
            case 5 -> "Friday";
            case 6 -> "Saturday";
            default -> "Unknown";
        };
    }
    private List<Interval> splitIntoIntervals(LocalTime inicio, LocalTime fin) {
        int fromMinute = inicio.toSecondOfDay() / 60;
        int toMinute = fin.toSecondOfDay() / 60;

        if (toMinute <= fromMinute) {
            // Overnight
            return List.of(
                new Interval(fromMinute, 1440),
                new Interval(0, toMinute)
            );
        } else {
            return List.of(new Interval(fromMinute, toMinute));
        }
    }
    private List<Integer> resolveDays(RecurrenceType recurrenceType, List<Integer> specificDays) {
    return switch (recurrenceType) {
        case EVERY_DAY -> List.of(0, 1, 2, 3, 4, 5, 6);
        case MONDAY_TO_FRIDAY -> List.of(1, 2, 3, 4, 5);
        case SPECIFIC_DAYS -> specificDays != null ? specificDays : Collections.emptyList();
    };
}
    // Utility record
    private record Interval(int desdeMin, int hastaMin) {
        boolean overlapsWith(Interval otro) {
            return this.desdeMin < otro.hastaMin && otro.desdeMin < this.hastaMin;
        }
    }
}
