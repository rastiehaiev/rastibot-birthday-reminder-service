package com.rastiehaiev.birthday.reminder.processor;

import com.rastiehaiev.birthday.reminder.model.BirthDayReminderStrategy;
import com.rastiehaiev.birthday.reminder.model.Notification;
import com.rastiehaiev.birthday.reminder.repository.BirthDayReminderRepository;
import com.rastiehaiev.birthday.reminder.repository.entity.BirthDayReminderEntity;
import com.rastiehaiev.birthday.reminder.service.BirthDayReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This class is responsible to process upcoming reminders and generate corresponding notifications.
 * For each particular reminder, an appropriate strategy is found unless preferred one is specified.
 * A service that will consume generated notifications will have all the necessary information to send it to user in appropriate way.
 * If reminder has expired, the next birthday timestamp is calculated to be processed in the next year.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BirthDayReminderProcessor {

    private final Clock clock;
    private final BirthDayReminderRepository repository;
    private final BirthDayReminderService reminderService;

    @Transactional
    public List<Notification> processBatch(int batchSize) {
        List<Notification> notifications = new ArrayList<>();
        List<BirthDayReminderEntity> expiredReminders = new ArrayList<>();

        Instant instantAtStartOfDay = clock.instant().truncatedTo(ChronoUnit.DAYS);
        long lastUpdatedMark = clock.instant().toEpochMilli() + TimeUnit.HOURS.toMillis(1);
        long upcomingBirthDaysTimestamp = instantAtStartOfDay.plus(BirthDayReminderStrategy.MAX_DAYS_AMOUNT + 1, ChronoUnit.DAYS).toEpochMilli();
        List<BirthDayReminderEntity> upcomingReminders = findUpcoming(lastUpdatedMark, upcomingBirthDaysTimestamp, batchSize);
        for (BirthDayReminderEntity reminder : upcomingReminders) {
            if (instantAtStartOfDay.toEpochMilli() > reminder.getNextBirthDayTimestamp()) {
                expiredReminders.add(reminder);
            } else {
                BirthDayReminderStrategy targetStrategy = findTargetStrategy(instantAtStartOfDay.toEpochMilli(), reminder.getNextBirthDayTimestamp());
                BirthDayReminderStrategy preferredStrategy = reminder.getPreferredStrategy();
                if (targetStrategy != null && (preferredStrategy == null || preferredStrategy.getDaysAmount() >= targetStrategy.getDaysAmount())) {
                    notifications.add(getNotificationFromReminder(reminder, targetStrategy));
                }
            }
            reminder.setLastUpdated(clock.instant().toEpochMilli());
        }

        processExpiredReminders(expiredReminders);
        repository.saveAll(upcomingReminders);
        return notifications;
    }

    private void processExpiredReminders(List<BirthDayReminderEntity> expiredReminders) {
        if (CollectionUtils.isNotEmpty(expiredReminders)) {
            log.info("Found {} expired reminders.", expiredReminders.size());
            for (BirthDayReminderEntity expiredReminder : expiredReminders) {
                long nextBirthdayTimestamp = reminderService.calculateNextBirthdayTimestamp(expiredReminder.getMonth(), expiredReminder.getDay());
                expiredReminder.setNextBirthDayTimestamp(nextBirthdayTimestamp);
            }
        }
    }

    private List<BirthDayReminderEntity> findUpcoming(long lastUpdatedMark, long upcomingBirthDaysTimestamp, int batchSize) {
        List<BirthDayReminderEntity> upcoming = repository.findUpcoming(upcomingBirthDaysTimestamp, lastUpdatedMark, PageRequest.of(0, batchSize));
        if (CollectionUtils.isNotEmpty(upcoming)) {
            log.info("Found {} upcoming reminders.", upcoming.size());
        }
        return upcoming;
    }

    private Notification getNotificationFromReminder(BirthDayReminderEntity reminder, BirthDayReminderStrategy targetStrategy) {
        Notification notification = new Notification();
        notification.setId(reminder.getId());
        notification.setChatId(reminder.getChatId());
        notification.setReminderUserChatId(reminder.getRemindedUserChatId());
        notification.setRemindedUserFirstName(reminder.getRemindedUserFirstName());
        notification.setRemindedUserLastName(reminder.getRemindedUserLastName());
        notification.setType(targetStrategy);
        notification.setDay(reminder.getDay());
        notification.setMonth(reminder.getMonth());
        notification.setYear(reminder.getYear());
        return notification;
    }

    private BirthDayReminderStrategy findTargetStrategy(long currentTimestamp, long nextBirthDayTimestamp) {
        int days = (int) TimeUnit.MILLISECONDS.toDays(nextBirthDayTimestamp - currentTimestamp);
        return BirthDayReminderStrategy.of(days);
    }
}

