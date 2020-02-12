package com.rastiehaiev.birthday.reminder.processor;

import com.rastiehaiev.birthday.reminder.repository.entity.BirthDayReminderEntity;
import com.rastiehaiev.birthday.reminder.model.BirthDayReminderStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

@Component
public class OnADayReminderStrategyProcessor extends ReminderStrategyProcessor {

    @Override
    public BirthDayReminderStrategy applicableStrategy() {
        return BirthDayReminderStrategy.ON_A_DAY;
    }

    @Override
    public boolean isApplicable(long millisBeforeNextReminder) {
        return millisBeforeNextReminder <= TimeUnit.DAYS.toMillis(1) && millisBeforeNextReminder > TimeUnit.DAYS.toMillis(0);
    }

    @Override
    protected int daysBeforeReminder() {
        return 0;
    }

    @Override
    protected void update(BirthDayReminderEntity reminder) {
        reminder.setStrategy(BirthDayReminderStrategy.TWO_WEEKS_BEFORE);
        LocalDate localDate = clock.instant().atOffset(ZoneOffset.UTC).toLocalDate();

        LocalDate nextDate = LocalDate.of(localDate.getYear() + 1, localDate.getMonth(), localDate.getDayOfMonth());
        reminder.setNextBirthDayTimestamp(nextDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());
    }
}
