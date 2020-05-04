package com.rastiehaiev.birthday.reminder.service;

import com.rastiehaiev.birthday.reminder.model.*;
import com.rastiehaiev.birthday.reminder.model.notification.Notification;
import com.rastiehaiev.birthday.reminder.model.notification.NotificationAction;
import com.rastiehaiev.birthday.reminder.model.notification.NotificationActionRequest;
import com.rastiehaiev.birthday.reminder.model.output.CreateBirthDayReminderSuccess;
import com.rastiehaiev.birthday.reminder.repository.BirthDayReminderRepository;
import com.rastiehaiev.birthday.reminder.repository.entity.BirthDayReminderEntity;
import com.rastiehaiev.birthday.reminder.utils.BirthdayReminderUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Clock;
import java.time.Instant;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BirthDayReminderService {

    private final Clock clock;
    private final BirthDayReminderRepository repository;
    private final NextBirthdayTimestampCalculator calculator;

    public ExistingReminder findExisting(long chatId, long remindedUserChatId) {
        BirthDayReminderEntity existingReminderEntity = repository.findByChatIdAndRemindedUserChatIdAndDeletedFalse(chatId, remindedUserChatId);
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

    public List<BirthdayReminderData> findAllOfKind(long chatId, ListBirthdayRemindersKind kind) {
        List<BirthDayReminderEntity> entities = findAllEntitiesOfKind(chatId, kind);
        return entities.stream()
                .map(this::toBirthdayReminderData)
                .collect(Collectors.toList());
    }

    private List<BirthDayReminderEntity> findAllEntitiesOfKind(long chatId, ListBirthdayRemindersKind kind) {
        switch (kind) {
            case ALL:
                Pageable pageable = PageRequest.of(0, 10);
                return repository.findAllByChatIdAndDeletedFalseOrderByNextBirthDayTimestamp(chatId, pageable);
            case UPCOMING:
                return Collections.singletonList(repository.findNearest(chatId));
            case NEXT_THREE:
                return repository.findThreeNearest(chatId);
            default:
                Month month = Month.valueOf(kind.name());
                return repository.findByMonth(chatId, month.getValue());
        }
    }

    private BirthdayReminderData toBirthdayReminderData(BirthDayReminderEntity entity) {
        BirthdayReminderData birthdayReminderData = new BirthdayReminderData();
        birthdayReminderData.setId(entity.getId());
        birthdayReminderData.setBirthday(getBirthday(entity));
        birthdayReminderData.setPerson(getPerson(entity));
        return birthdayReminderData;
    }

    public CreateBirthDayReminderSuccess create(BirthDayReminder birthDayReminder) {
        long chatId = birthDayReminder.getChatId();
        long remindedUserChatId = birthDayReminder.getPerson().getChatId();

        BirthDayReminderEntity reminder = repository.findByChatIdAndRemindedUserChatId(chatId, remindedUserChatId);
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

    @Transactional
    public void markAsDeleted(long reminderId) {
        repository.findById(reminderId)
                .ifPresent(reminder -> {
                    reminder.setDeleted(true);
                    reminder.setDisabled(false);
                });
    }

    private BirthDayReminder toReminder(BirthDayReminderEntity existingReminder) {
        BirthDayReminder reminder = new BirthDayReminder();
        reminder.setChatId(existingReminder.getChatId());
        reminder.setDay(existingReminder.getDay());
        reminder.setMonth(existingReminder.getMonth());
        reminder.setYear(existingReminder.getYear());
        reminder.setPerson(getPerson(existingReminder));
        return reminder;
    }

    private Birthday getBirthday(BirthDayReminderEntity existingReminderEntity) {
        Birthday birthday = new Birthday();
        birthday.setDay(existingReminderEntity.getDay());
        birthday.setMonth(Month.of(existingReminderEntity.getMonth()));
        birthday.setYear(existingReminderEntity.getYear());
        return birthday;
    }

    private Person getPerson(BirthDayReminderEntity existingReminder) {
        Person person = new Person();
        person.setChatId(existingReminder.getRemindedUserChatId());
        person.setFirstName(existingReminder.getRemindedUserFirstName());
        person.setLastName(existingReminder.getRemindedUserLastName());
        return person;
    }

    public long countAll() {
        return repository.count();
    }

    public long countNotDeleted() {
        return repository.countAllByDeletedFalse();
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
