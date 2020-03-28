package com.rastiehaiev.birthday.reminder.component;

import com.rastiehaiev.birthday.reminder.model.BirthDayReminderStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class TargetStrategyResolver {

    private final Clock clock;

    public BirthDayReminderStrategy resolve(long nextBirthdayTimestamp) {
        Instant instantAtStartOfDay = clock.instant().truncatedTo(ChronoUnit.DAYS);
        int days = (int) TimeUnit.MILLISECONDS.toDays(nextBirthdayTimestamp - instantAtStartOfDay.toEpochMilli());
        return BirthDayReminderStrategy.of(days);
    }
}
