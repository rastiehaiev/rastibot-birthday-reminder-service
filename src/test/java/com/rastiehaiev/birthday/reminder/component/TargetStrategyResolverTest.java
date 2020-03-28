package com.rastiehaiev.birthday.reminder.component;

import com.rastiehaiev.birthday.reminder.model.BirthDayReminderStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TargetStrategyResolverTest {

    // March 28, 2020 10:00:17 AM (GMT)
    public static final long START_FROM_TIMESTAMP = 1585389617000L;

    private Clock clock = mock(Clock.class);
    private TargetStrategyResolver resolver = new TargetStrategyResolver(clock);

    @BeforeEach
    public void beforeEach() {
        when(clock.instant()).thenReturn(Instant.ofEpochMilli(START_FROM_TIMESTAMP));
    }

    @Test
    public void shouldReturnTwoWeeksBefore() {

        long nextBirthdayTimestamp = 1586606400000L; // April 11, 2020 12:00:00 PM (GMT)

        BirthDayReminderStrategy strategy = resolver.resolve(nextBirthdayTimestamp);
        assertEquals(BirthDayReminderStrategy.TWO_WEEKS_BEFORE, strategy);
    }

    @Test
    public void shouldReturnWeekBefore() {

        long nextBirthdayTimestamp = 1586001600000L; // April 4, 2020 12:00:00 PM (GMT)

        BirthDayReminderStrategy strategy = resolver.resolve(nextBirthdayTimestamp);
        assertEquals(BirthDayReminderStrategy.A_WEEK_BEFORE, strategy);
    }

    @Test
    public void shouldReturnThreeDaysBefore() {

        long nextBirthdayTimestamp = 1585656000000L; // March 31, 2020 12:00:00 PM (GMT)

        BirthDayReminderStrategy strategy = resolver.resolve(nextBirthdayTimestamp);
        assertEquals(BirthDayReminderStrategy.THREE_DAYS_BEFORE, strategy);
    }

    @Test
    public void shouldReturnDayBefore() {

        long nextBirthdayTimestamp = 1585483200000L; // March 29, 2020 12:00:00 PM (GMT)

        BirthDayReminderStrategy strategy = resolver.resolve(nextBirthdayTimestamp);
        assertEquals(BirthDayReminderStrategy.A_DAY_BEFORE, strategy);
    }

    @Test
    public void shouldReturnOnDay() {

        long nextBirthdayTimestamp = 1585396800000L; // March 28, 2020 12:00:00 PM (GMT)

        BirthDayReminderStrategy strategy = resolver.resolve(nextBirthdayTimestamp);
        assertEquals(BirthDayReminderStrategy.ON_A_DAY, strategy);
    }

    @Test
    public void shouldReturnNull() {
        long nextBirthdayTimestamp = 1585569600000L; // March 30, 2020 12:00:00 PM (GMT)

        BirthDayReminderStrategy strategy = resolver.resolve(nextBirthdayTimestamp);
        assertNull(strategy);
    }
}