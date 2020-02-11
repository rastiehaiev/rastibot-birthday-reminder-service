package birthday.reminder.service.processor;

import birthday.reminder.service.entity.BirthDayReminderEntity;
import birthday.reminder.service.model.BirthDayReminderStrategy;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ThreeDaysBeforeReminderStrategyProcessor extends ReminderStrategyProcessor {

    @Override
    public BirthDayReminderStrategy applicableStrategy() {
        return BirthDayReminderStrategy.THREE_DAYS_BEFORE;
    }

    @Override
    protected int daysBeforeReminder() {
        return 3;
    }

    @Override
    protected void update(BirthDayReminderEntity reminder) {
        reminder.setStrategy(BirthDayReminderStrategy.A_DAY_BEFORE);
    }

    @Override
    public boolean isApplicable(long millisBeforeNextReminder) {
        return millisBeforeNextReminder <= TimeUnit.DAYS.toMillis(7) && millisBeforeNextReminder > TimeUnit.DAYS.toMillis(3);
    }
}
