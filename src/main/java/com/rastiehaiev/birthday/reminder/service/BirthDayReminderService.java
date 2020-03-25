package com.rastiehaiev.birthday.reminder.service;

import com.rastiehaiev.birthday.reminder.exception.ReminderAlreadyExistsException;
import com.rastiehaiev.birthday.reminder.model.*;
import com.rastiehaiev.birthday.reminder.model.notification.NotificationAction;
import com.rastiehaiev.birthday.reminder.model.notification.NotificationActionRequest;
import com.rastiehaiev.birthday.reminder.model.output.CreateBirthDayReminderSuccess;
import com.rastiehaiev.birthday.reminder.repository.BirthDayReminderRepository;
import com.rastiehaiev.birthday.reminder.repository.entity.BirthDayReminderEntity;
import com.rastiehaiev.birthday.reminder.utils.BirthdayReminderUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.*;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class BirthDayReminderService {

    private final Clock clock;
    private final BirthDayReminderRepository repository;

    public ExistingReminder findExisting(long chatId, long remindedUserChatId) {
        BirthDayReminderEntity existingReminderEntity = repository.findByChatIdAndRemindedUserChatId(chatId, remindedUserChatId);
        if (existingReminderEntity != null) {
            ExistingReminder existingReminder = new ExistingReminder();
            existingReminder.setBirthday(getBirthday(existingReminderEntity));
            return existingReminder;
        }
        return null;
    }

    public CreateBirthDayReminderSuccess create(BirthDayReminder birthDayReminder) {
        long chatId = birthDayReminder.getChatId();
        long remindedUserChatId = birthDayReminder.getPerson().getChatId();

        // check the parallel execution of the same request
        synchronized ((chatId + "_" + remindedUserChatId).intern()) {
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

            long nextBirthDayTimestamp = calculateNextBirthdayTimestamp(birthDayReminder.getMonth(), birthDayReminder.getDay());
            reminder.setNextBirthDayTimestamp(nextBirthDayTimestamp);
            reminder.setPreferredStrategy(null);

            log.debug("Saving reminder entity: {}", reminder);
            repository.save(reminder);
            return new CreateBirthDayReminderSuccess(nextBirthDayTimestamp, birthDayReminder);
        }
    }

    public long calculateNextBirthdayTimestamp(int monthNumber, int day) {
        OffsetDateTime offsetDateTime = clock.instant().truncatedTo(ChronoUnit.DAYS).atOffset(ZoneOffset.UTC);

        int currentYear = offsetDateTime.getYear();
        Month month = Month.of(monthNumber);
        LocalDate birthday = getLocalDate(currentYear, month, day);
        if (birthday.isBefore(offsetDateTime.toLocalDate())) {
            birthday = getLocalDate(currentYear + 1, month, day);
        }
        return birthday.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
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

    private LocalDate getLocalDate(int currentYear, Month month, int day) {
        if (month == Month.FEBRUARY && day == 29 && !Year.isLeap(currentYear)) {
            day = 28;
        }
        return LocalDate.of(currentYear, month.getValue(), day);
    }
}
