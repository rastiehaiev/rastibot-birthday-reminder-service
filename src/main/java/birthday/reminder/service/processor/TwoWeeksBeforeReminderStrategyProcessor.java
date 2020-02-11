package birthday.reminder.service.processor;

import birthday.reminder.service.entity.BirthDayReminderEntity;
import birthday.reminder.service.model.BirthDayReminderStrategy;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class TwoWeeksBeforeReminderStrategyProcessor extends ReminderStrategyProcessor {

    @Override
    public BirthDayReminderStrategy applicableStrategy() {
        return BirthDayReminderStrategy.TWO_WEEKS_BEFORE;
    }

    @Override
    protected int daysBeforeReminder() {
        return 14;
    }

    @Override
    protected void update(BirthDayReminderEntity reminder) {
        reminder.setStrategy(BirthDayReminderStrategy.A_WEEK_BEFORE);
    }

    @Override
    public boolean isApplicable(long millisBeforeNextReminder) {
        return millisBeforeNextReminder > TimeUnit.DAYS.toMillis(14);
    }
}
