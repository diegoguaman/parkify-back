package com.igrowker.feature.parkify.features.parkingV2.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.igrowker.feature.parkify.features.parkingV2.dto.request.ShiftRequestDTO;
import com.igrowker.feature.parkify.features.parkingV2.entities.RecurrenceType;
import com.igrowker.feature.parkify.features.parkingV2.entities.Shift;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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

        for (Shift existing : existingShifts) {
            List<Interval> currentIntervals = splitIntoIntervals(
                existing.getStartTime(), existing.getEndTime());

            for (Interval newInterval : newIntervals) {
                for (Interval actual : currentIntervals) {
                    if (newInterval.overlapsWith(actual)) {
                        errors.add("The shift overlaps with an existing one: " +
                            (existing.getName() != null ? existing.getName() : "unnamed") +
                            " (" + existing.getStartTime() + " - " + existing.getEndTime() + ")");
                        return errors;
                    }
                }
            }
        }

        return errors;
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

    // Utility record
    private record Interval(int desdeMin, int hastaMin) {
        boolean overlapsWith(Interval otro) {
            return this.desdeMin < otro.hastaMin && otro.desdeMin < this.hastaMin;
        }
    }
}
