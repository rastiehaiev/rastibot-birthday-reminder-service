package birthday.reminder.service.processor;

import birthday.reminder.service.entity.BirthDayReminderEntity;
import birthday.reminder.service.model.BirthDayReminderStrategy;
import birthday.reminder.service.model.Notification;
import birthday.reminder.service.repository.BirthDayReminderRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;

import javax.transaction.Transactional;
import java.time.Clock;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public abstract class ReminderStrategyProcessor {

    @Autowired
    protected Clock clock;
    @Autowired
    protected BirthDayReminderRepository repository;

    @Value("${birthday-reminder-service.batch-size:100}")
    private int batchSize;

    @Transactional
    public List<Notification> process() {
        List<Notification> notifications = new ArrayList<>();
        List<BirthDayReminderEntity> targetReminders = findTargetReminders(batchSize);
        if (CollectionUtils.isNotEmpty(targetReminders)) {
            for (BirthDayReminderEntity reminder : targetReminders) {
                update(reminder);
                repository.save(reminder);
                notifications.add(getNotificationFromReminder(reminder));
            }
        }
        return notifications;
    }

    public abstract BirthDayReminderStrategy applicableStrategy();

    public abstract boolean isApplicable(long millisBeforeNextReminder);

    protected abstract int daysBeforeReminder();

    protected abstract void update(BirthDayReminderEntity reminder);

    private List<BirthDayReminderEntity> findTargetReminders(int batchSize) {
        long inDays = clock.instant().plus(daysBeforeReminder() + 1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS).toEpochMilli();
        return repository.findApplicableByStrategyAndNextBirthDayTimestamp(applicableStrategy().toString(), inDays, PageRequest.of(0, batchSize));
    }

    private Notification getNotificationFromReminder(BirthDayReminderEntity reminder) {
        Notification notification = new Notification();
        notification.setId(reminder.getId());
        notification.setChatId(reminder.getChatId());
        notification.setReminderUserChatId(reminder.getRemindedUserChatId());
        notification.setRemindedUserFirstName(reminder.getRemindedUserFirstName());
        notification.setRemindedUserLastName(reminder.getRemindedUserLastName());
        notification.setStrategy(applicableStrategy());
        return notification;
    }
}
