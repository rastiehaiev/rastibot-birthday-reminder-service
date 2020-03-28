package com.rastiehaiev.birthday.reminder.service;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NextBirthdayTimestampCalculatorTest {

    private Clock clock = mock(Clock.class);
    private NextBirthdayTimestampCalculator calculator = new NextBirthdayTimestampCalculator(clock);

    @Test
    public void shouldCalculateNextTimestamp_whenSpecifiedMonthInTheFuture() {
        long timestamp = 1585389617000L; // March 28, 2020 10:00:17 AM (GMT)
        when(clock.instant()).thenReturn(Instant.ofEpochMilli(timestamp));

        long actual = calculator.nextBirthdayTimestamp(4, 2);

        // April 2, 2020 12:00:00 AM (GMT)
        assertEquals(1585785600000L, actual);
    }

    @Test
    public void shouldCalculateNextTimestamp_whenSpecifiedMonthInThePast() {
        long timestamp = 1585389617000L; // March 28, 2020 10:00:17 AM (GMT)
        when(clock.instant()).thenReturn(Instant.ofEpochMilli(timestamp));

        long actual = calculator.nextBirthdayTimestamp(2, 1);

        // February 1, 2021 12:00:00 AM (GMT)
        assertEquals(1612137600000L, actual);
    }

    @Test
    public void shouldReturnFebruaryTwentyNine_whenLeapYear() {
        long timestamp = 1574780580000L; // November 26, 2019 3:03:00 PM (GMT)
        when(clock.instant()).thenReturn(Instant.ofEpochMilli(timestamp));

        long actual = calculator.nextBirthdayTimestamp(2, 29);

        // February 29, 2020 12:00:00 AM (GMT)
        assertEquals(1582934400000L, actual);
    }

    @Test
    public void shouldReturnFebruaryTwentyEight_whenNotLeapYear() {
        long timestamp = 1588122720000L; // April 29, 2020 1:12:00 AM
        when(clock.instant()).thenReturn(Instant.ofEpochMilli(timestamp));

        long actual = calculator.nextBirthdayTimestamp(2, 29);

        // February 28, 2021 12:00:00 AM
        assertEquals(1614470400000L, actual);
    }
}