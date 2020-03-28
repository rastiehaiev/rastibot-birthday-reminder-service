package com.rastiehaiev.birthday.reminder.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.*;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class NextBirthdayTimestampCalculator {

    private final Clock clock;

    public long nextBirthdayTimestamp(int monthNumber, int day) {
        OffsetDateTime offsetDateTime = clock.instant().truncatedTo(ChronoUnit.DAYS).atOffset(ZoneOffset.UTC);

        int currentYear = offsetDateTime.getYear();
        Month month = Month.of(monthNumber);
        LocalDate birthday = getLocalDate(currentYear, month, day);
        if (birthday.isBefore(offsetDateTime.toLocalDate())) {
            birthday = getLocalDate(currentYear + 1, month, day);
        }
        return birthday.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    private LocalDate getLocalDate(int currentYear, Month month, int day) {
        if (month == Month.FEBRUARY && day == 29 && !Year.isLeap(currentYear)) {
            day = 28;
        }
        return LocalDate.of(currentYear, month.getValue(), day);
    }
}
