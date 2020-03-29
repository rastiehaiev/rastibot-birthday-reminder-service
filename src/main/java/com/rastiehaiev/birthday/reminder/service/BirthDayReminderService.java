package com.rastiehaiev.birthday.reminder.service;

import com.rastiehaiev.birthday.reminder.exception.ReminderAlreadyExistsException;
import com.rastiehaiev.birthday.reminder.model.*;
import com.rastiehaiev.birthday.reminder.model.notification.Notification;
import com.rastiehaiev.birthday.reminder.model.notification.NotificationAction;
import com.rastiehaiev.birthday.reminder.model.notification.NotificationActionRequest;
import com.rastiehaiev.birthday.reminder.model.output.CreateBirthDayReminderSuccess;
import com.rastiehaiev.birthday.reminder.properties.RastibotBirthDayReminderServiceScheduleProperties;
import com.rastiehaiev.birthday.reminder.repository.BirthDayReminderRepository;
import com.rastiehaiev.birthday.reminder.repository.entity.BirthDayReminderEntity;
import com.rastiehaiev.birthday.reminder.utils.BirthdayReminderUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Clock;
import java.time.Instant;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class BirthDayReminderService {

    private final Clock clock;
    private final BirthDayReminderRepository repository;
    private final NextBirthdayTimestampCalculator calculator;
    private final RastibotBirthDayReminderServiceScheduleProperties properties;

    public ExistingReminder findExisting(long chatId, long remindedUserChatId) {
        BirthDayReminderEntity existingReminderEntity = repository.findByChatIdAndRemindedUserChatId(chatId, remindedUserChatId);
        if (existingReminderEntity != null) {
            ExistingReminder existingReminder = new ExistingReminder();
            existingReminder.setBirthday(getBirthday(existingReminderEntity));
            return existingReminder;
        }
        return null;
    }

    public List<BirthDayReminderEntity> findUpcoming(int batchSize) {
        Instant instant = clock.instant();
        Instant instantAtStartOfDay = instant.truncatedTo(ChronoUnit.DAYS);
        long upcomingBirthDaysTimestamp = instantAtStartOfDay.plus(BirthDayReminderStrategy.MAX_DAYS_AMOUNT + 1, ChronoUnit.DAYS).toEpochMilli();
        long lastUpdatedTimestamp = instant.toEpochMilli() - TimeUnit.HOURS.toMillis(1);
        List<BirthDayReminderEntity> upcoming = repository.findUpcoming(upcomingBirthDaysTimestamp, lastUpdatedTimestamp, PageRequest.of(0, batchSize));
        if (CollectionUtils.isNotEmpty(upcoming)) {
            log.info("Found {} upcoming reminders.", upcoming.size());
        }
        return upcoming;
    }

    public CreateBirthDayReminderSuccess create(BirthDayReminder birthDayReminder) {
        long chatId = birthDayReminder.getChatId();
        long remindedUserChatId = birthDayReminder.getPerson().getChatId();

        BirthDayReminderEntity reminder = repository.findByChatIdAndRemindedUserChatId(chatId, remindedUserChatId);
        if (reminder != null && !birthDayReminder.isOverride()) {
            throw new ReminderAlreadyExistsException("Reminder already exists.", toReminder(reminder));
        }

        if (reminder == null) {
            reminder = new BirthDayReminderEntity();
        }

        reminder.setChatId(birthDayReminder.getChatId());
        reminder.setDay(birthDayReminder.getDay());
        reminder.setMonth(birthDayReminder.getMonth());
        reminder.setYear(birthDayReminder.getYear());
        reminder.setRemindedUserChatId(remindedUserChatId);
        reminder.setRemindedUserFirstName(birthDayReminder.getPerson().getFirstName());
        reminder.setRemindedUserLastName(birthDayReminder.getPerson().getLastName());
        reminder.setDisabled(false);
        reminder.setDeleted(false);
        reminder.setLastUpdated(null);

        long nextBirthDayTimestamp = calculator.nextBirthdayTimestamp(birthDayReminder.getMonth(), birthDayReminder.getDay());
        reminder.setNextBirthDayTimestamp(nextBirthDayTimestamp);
        reminder.setPreferredStrategy(null);

        repository.save(reminder);
        log.info("Created reminder: {}.", reminder);
        return new CreateBirthDayReminderSuccess(nextBirthDayTimestamp, birthDayReminder);
    }

    public void update(List<BirthDayReminderEntity> reminders) {
        repository.saveAll(reminders);
    }

    @Transactional
    public Person reactOnNotificationAction(NotificationActionRequest request) {
        NotificationAction action = NotificationAction.from(request.getAction());
        if (action == null) {
            log.warn("Could not resolve action by string [{}].", request.getAction());
            return null;
        }
        BirthDayReminderEntity entity = repository.findById(request.getNotificationId()).orElse(null);
        if (entity == null) {
            log.warn("Failed to react no notification action. Could not find reminder by ID={}.", request.getNotificationId());
            return null;
        }
        switch (action) {
            case DO_NOT_NOTIFY_ANYMORE:
                entity.setDeleted(true);
                entity.setDisabled(false);
                break;
            case DO_NOT_NOTIFY_THIS_YEAR:
                entity.setDisabled(true);
                entity.setDeleted(false);
                break;
            default:
                BirthDayReminderStrategy strategy = BirthDayReminderStrategy.of(action.getSupportedDaysBefore());
                entity.setPreferredStrategy(strategy);
                entity.setDisabled(false);
                entity.setDeleted(false);
        }
        repository.save(entity);
        return BirthdayReminderUtils.toPerson(entity);
    }

    private Birthday getBirthday(BirthDayReminderEntity existingReminderEntity) {
        Birthday birthday = new Birthday();
        birthday.setDay(existingReminderEntity.getDay());
        birthday.setMonth(Month.of(existingReminderEntity.getMonth()));
        birthday.setYear(existingReminderEntity.getYear());
        return birthday;
    }

    private BirthDayReminder toReminder(BirthDayReminderEntity existingReminder) {
        Person person = new Person();
        person.setChatId(existingReminder.getRemindedUserChatId());
        person.setFirstName(existingReminder.getRemindedUserFirstName());
        person.setLastName(existingReminder.getRemindedUserLastName());

        BirthDayReminder reminder = new BirthDayReminder();
        reminder.setChatId(existingReminder.getChatId());
        reminder.setDay(existingReminder.getDay());
        reminder.setMonth(existingReminder.getMonth());
        reminder.setYear(existingReminder.getYear());
        reminder.setPerson(person);
        return reminder;
    }

    public Long countAll() {
        return repository.count();
    }

    public void postProcessNotifications(List<Notification> notifications) {
        notifications.forEach(this::postProcessNotification);
    }

    private void postProcessNotification(Notification notification) {
        int daysAmount = notification.getType().getDaysAmount();
        log.info("Update last notified days to {} for reminder with ID={}.", daysAmount, notification.getId());
        repository.updateLastNotifiedDays(notification.getId(), daysAmount);
    }
}
