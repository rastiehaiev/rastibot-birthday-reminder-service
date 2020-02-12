package com.rastiehaiev.birthday.reminder.processor;

import com.rastiehaiev.birthday.reminder.repository.entity.BirthDayReminderEntity;
import com.rastiehaiev.birthday.reminder.model.BirthDayReminderStrategy;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ThreeDaysBeforeReminderStrategyProcessor extends ReminderStrategyProcessor {

    @Override
    public BirthDayReminderStrategy applicableStrategy() {
        return BirthDayReminderStrategy.THREE_DAYS_BEFORE;
    }

    @Override
    public boolean isApplicable(long millisBeforeNextReminder) {
        return millisBeforeNextReminder <= TimeUnit.DAYS.toMillis(7) && millisBeforeNextReminder > TimeUnit.DAYS.toMillis(3);
    }

    @Override
    protected int daysBeforeReminder() {
        return 3;
    }

    @Override
    protected void update(BirthDayReminderEntity reminder) {
        reminder.setStrategy(BirthDayReminderStrategy.A_DAY_BEFORE);
    }
}
