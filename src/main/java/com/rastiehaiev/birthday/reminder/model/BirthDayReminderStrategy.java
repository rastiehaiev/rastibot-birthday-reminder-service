package com.rastiehaiev.birthday.reminder.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BirthDayReminderStrategy {

    TWO_WEEKS_BEFORE(14),
    A_WEEK_BEFORE(7),
    THREE_DAYS_BEFORE(3),
    A_DAY_BEFORE(1),
    ON_A_DAY(0);

    @Getter
    private final int daysAmount;

    public static BirthDayReminderStrategy of(int days) {
        for (BirthDayReminderStrategy value : BirthDayReminderStrategy.values()) {
            if (value.daysAmount == days) {
                return value;
            }
        }
        return null;
    }
}
