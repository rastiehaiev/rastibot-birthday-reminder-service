package birthday.reminder.service.processor;

import birthday.reminder.service.entity.BirthDayReminderEntity;
import birthday.reminder.service.model.BirthDayReminderStrategy;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class AWeekBeforeReminderStrategyProcessor extends ReminderStrategyProcessor {

    @Override
    public BirthDayReminderStrategy applicableStrategy() {
        return BirthDayReminderStrategy.A_WEEK_BEFORE;
    }

    @Override
    public boolean isApplicable(long millisBeforeNextReminder) {
        return millisBeforeNextReminder <= TimeUnit.DAYS.toMillis(14) && millisBeforeNextReminder > TimeUnit.DAYS.toMillis(7);
    }

    @Override
    protected int daysBeforeReminder() {
        return 7;
    }

    @Override
    protected void update(BirthDayReminderEntity reminder) {
        reminder.setStrategy(BirthDayReminderStrategy.THREE_DAYS_BEFORE);
    }
}
