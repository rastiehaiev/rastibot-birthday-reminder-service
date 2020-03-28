package com.rastiehaiev.birthday.reminder.processor;

import com.rastiehaiev.birthday.reminder.component.TargetStrategyResolver;
import com.rastiehaiev.birthday.reminder.model.BirthDayReminderStrategy;
import com.rastiehaiev.birthday.reminder.model.notification.Notification;
import com.rastiehaiev.birthday.reminder.model.notification.NotificationAction;
import com.rastiehaiev.birthday.reminder.repository.entity.BirthDayReminderEntity;
import com.rastiehaiev.birthday.reminder.service.BirthDayReminderService;
import com.rastiehaiev.birthday.reminder.service.NextBirthdayTimestampCalculator;
import com.rastiehaiev.birthday.reminder.utils.BirthdayReminderUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final BirthDayReminderService reminderService;
    private final NextBirthdayTimestampCalculator calculator;
    private final TargetStrategyResolver targetStrategyResolver;

    @Transactional
    public List<Notification> processBatch(int batchSize) {
        List<Notification> notifications = new ArrayList<>();
        List<BirthDayReminderEntity> expiredReminders = new ArrayList<>();

        Instant instantAtStartOfDay = clock.instant().truncatedTo(ChronoUnit.DAYS);
        log.info("Current timestamp at start of the day: {}.", instantAtStartOfDay.toEpochMilli());
        List<BirthDayReminderEntity> upcomingReminders = reminderService.findUpcoming(batchSize);
        for (BirthDayReminderEntity reminder : upcomingReminders) {
            log.info("Processing reminder {}.", reminder);
            if (instantAtStartOfDay.toEpochMilli() > reminder.getNextBirthDayTimestamp()) {
                log.info("Reminder has expired: {}.", reminder);
                expiredReminders.add(reminder);
            } else {
                Optional<Notification> notification = tryToCreateNotification(reminder);
                notification.ifPresent(notifications::add);
            }
            reminder.setLastUpdated(clock.instant().toEpochMilli());
        }

        processExpiredReminders(expiredReminders);
        reminderService.update(upcomingReminders);
        log.info("Created {} notifications.", notifications.size());
        return notifications;
    }

    private Optional<Notification> tryToCreateNotification(BirthDayReminderEntity reminder) {
        if (reminder.isDisabled()) {
            log.info("Reminder {} is disabled.", reminder);
            return Optional.empty();
        }
        BirthDayReminderStrategy targetStrategy = targetStrategyResolver.resolve(reminder.getNextBirthDayTimestamp());
        if (targetStrategy == null) {
            log.info("Reminder {} is not to be processed yet.", reminder);
            return Optional.empty();
        }
        BirthDayReminderStrategy preferredStrategy = reminder.getPreferredStrategy();
        if (preferredStrategy != null) {
            if (preferredStrategy.getDaysAmount() < targetStrategy.getDaysAmount()) {
                log.info("Reminder: {}. Preferred strategy '{}' is less than target one {}. Skipping for now.",
                        reminder, preferredStrategy, targetStrategy);
                return Optional.empty();
            }
        }
        return Optional.of(getNotificationFromReminder(reminder, targetStrategy));
    }

    private void processExpiredReminders(List<BirthDayReminderEntity> expiredReminders) {
        if (CollectionUtils.isNotEmpty(expiredReminders)) {
            log.info("Found {} expired reminders.", expiredReminders.size());
            for (BirthDayReminderEntity expiredReminder : expiredReminders) {
                long nextBirthdayTimestamp = calculator.nextBirthdayTimestamp(expiredReminder.getMonth(), expiredReminder.getDay());
                expiredReminder.setNextBirthDayTimestamp(nextBirthdayTimestamp);
                expiredReminder.setDisabled(false);
            }
        }
    }

    private Notification getNotificationFromReminder(BirthDayReminderEntity reminder, BirthDayReminderStrategy targetStrategy) {
        Notification notification = new Notification();
        notification.setId(reminder.getId());
        notification.setChatId(reminder.getChatId());
        notification.setPerson(BirthdayReminderUtils.toPerson(reminder));
        notification.setType(targetStrategy);
        notification.setDay(reminder.getDay());
        notification.setMonth(reminder.getMonth());
        notification.setYear(reminder.getYear());
        notification.setActions(getAvailableActions(targetStrategy));
        return notification;
    }

    private List<String> getAvailableActions(BirthDayReminderStrategy strategy) {
        return Stream.of(NotificationAction.values())
                .filter(action -> action.getSupportedDaysBefore() < strategy.getDaysAmount())
                .map(NotificationAction::getAbbreviation)
                .collect(Collectors.toList());
    }
}

