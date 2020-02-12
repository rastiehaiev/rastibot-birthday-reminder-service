package com.rastiehaiev.birthday.reminder.service;

import com.rastiehaiev.birthday.reminder.exception.ReminderAlreadyExistsException;
import com.rastiehaiev.birthday.reminder.model.BirthDayReminder;
import com.rastiehaiev.birthday.reminder.model.output.CreateBirthDayReminderResult;
import com.rastiehaiev.birthday.reminder.repository.BirthDayReminderRepository;
import com.rastiehaiev.birthday.reminder.repository.entity.BirthDayReminderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class BirthDayReminderService {

    private final Clock clock;
    private final BirthDayReminderRepository repository;

    public CreateBirthDayReminderResult create(BirthDayReminder birthDayReminder) {
        long chatId = birthDayReminder.getChatId();
        long remindedUserChatId = birthDayReminder.getPerson().getChatId();

        // check the parallel execution of the same request
        synchronized ((chatId + "_" + remindedUserChatId).intern()) {
            BirthDayReminderEntity existingReminder = repository.findByChatIdAndRemindedUserChatId(chatId, remindedUserChatId);
            if (existingReminder != null) {
                throw new ReminderAlreadyExistsException("Reminder already exists.");
            }

            BirthDayReminderEntity reminder = new BirthDayReminderEntity();
            reminder.setChatId(birthDayReminder.getChatId());
            reminder.setDay(birthDayReminder.getDay());
            reminder.setMonth(birthDayReminder.getMonth());
            reminder.setYear(birthDayReminder.getYear());
            reminder.setRemindedUserChatId(remindedUserChatId);
            reminder.setRemindedUserFirstName(birthDayReminder.getPerson().getFirstName());
            reminder.setRemindedUserLastName(birthDayReminder.getPerson().getLastName());

            long nextBirthDayTimestamp = calculateNextBirthdayTimestamp(birthDayReminder.getMonth(), birthDayReminder.getDay());
            reminder.setNextBirthDayTimestamp(nextBirthDayTimestamp);
            repository.save(reminder);
            return new CreateBirthDayReminderResult(nextBirthDayTimestamp);
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

    private LocalDate getLocalDate(int currentYear, Month month, int day) {
        if (month == Month.FEBRUARY && day == 29 && !Year.isLeap(currentYear)) {
            day = 28;
        }
        return LocalDate.of(currentYear, month.getValue(), day);
    }
}
