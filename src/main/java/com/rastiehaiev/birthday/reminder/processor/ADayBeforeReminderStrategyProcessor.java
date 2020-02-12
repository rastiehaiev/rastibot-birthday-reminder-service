package com.rastiehaiev.birthday.reminder.processor;

import com.rastiehaiev.birthday.reminder.repository.entity.BirthDayReminderEntity;
import com.rastiehaiev.birthday.reminder.model.BirthDayReminderStrategy;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ADayBeforeReminderStrategyProcessor extends ReminderStrategyProcessor {

    @Override
    public BirthDayReminderStrategy applicableStrategy() {
        return BirthDayReminderStrategy.A_DAY_BEFORE;
    }

    @Override
    public boolean isApplicable(long millisBeforeNextReminder) {
        return millisBeforeNextReminder <= TimeUnit.DAYS.toMillis(3) && millisBeforeNextReminder > TimeUnit.DAYS.toMillis(1);
    }

    @Override
    protected int daysBeforeReminder() {
        return 1;
    }

    @Override
    protected void update(BirthDayReminderEntity reminder) {
        reminder.setStrategy(BirthDayReminderStrategy.ON_A_DAY);
    }
}