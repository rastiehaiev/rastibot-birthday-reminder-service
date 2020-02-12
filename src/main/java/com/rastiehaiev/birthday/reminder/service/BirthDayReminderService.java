package com.rastiehaiev.birthday.reminder.service;

import com.rastiehaiev.birthday.reminder.repository.entity.BirthDayReminderEntity;
import com.rastiehaiev.birthday.reminder.exception.ReminderAlreadyExistsException;
import com.rastiehaiev.birthday.reminder.model.BirthDayReminder;
import com.rastiehaiev.birthday.reminder.model.BirthDayReminderStrategy;
import com.rastiehaiev.birthday.reminder.model.output.CreateBirthDayReminderResult;
import com.rastiehaiev.birthday.reminder.processor.ReminderStrategyProcessor;
import com.rastiehaiev.birthday.reminder.repository.BirthDayReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BirthDayReminderService {

    private final Clock clock;
    private final BirthDayReminderRepository repository;
    private final List<ReminderStrategyProcessor> processors;

    public CreateBirthDayReminderResult create(BirthDayReminder birthDayReminder) {
        long chatId = birthDayReminder.getChatId();
        long remindedUserChatId = birthDayReminder.getPerson().getChatId();
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

        BirthDayReminderStrategyAndTime strategyAndTime = calculateNextBirthDayStrategyAndTime(birthDayReminder.getMonth(), birthDayReminder.getDay());
        reminder.setNextBirthDayTimestamp(strategyAndTime.nextBirthDayTimestamp);
        reminder.setStrategy(strategyAndTime.strategy);
        repository.save(reminder);
        return new CreateBirthDayReminderResult(strategyAndTime.nextBirthDayTimestamp);
    }

    private BirthDayReminderStrategyAndTime calculateNextBirthDayStrategyAndTime(int monthNumber, int day) {
        Instant instant = clock.instant();
        Instant truncatedToDayInstant = instant.truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime zonedDateTime = truncatedToDayInstant.atZone(ZoneId.systemDefault());

        int currentYear = zonedDateTime.getYear();
        long currentMillis = truncatedToDayInstant.toEpochMilli();

        Month month = Month.of(monthNumber);
        LocalDate birthday = LocalDate.of(currentYear, month.getValue(), day);
        if (birthday.isBefore(zonedDateTime.toLocalDate())) {
            birthday = LocalDate.of(currentYear + 1, month.getValue(), day);
        }
        long nextBirthDayTimestamp = birthday.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
        BirthDayReminderStrategy strategy = findApplicableStrategy(nextBirthDayTimestamp - currentMillis);
        return new BirthDayReminderStrategyAndTime(strategy, nextBirthDayTimestamp);
    }

    private BirthDayReminderStrategy findApplicableStrategy(long millisBeforeNextReminder) {
        return processors.stream()
                .filter(processor -> processor.isApplicable(millisBeforeNextReminder))
                .findFirst()
                .map(ReminderStrategyProcessor::applicableStrategy)
                .orElse(BirthDayReminderStrategy.TWO_WEEKS_BEFORE);
    }

    @RequiredArgsConstructor
    private static class BirthDayReminderStrategyAndTime {
        private final BirthDayReminderStrategy strategy;
        private final long nextBirthDayTimestamp;
    }
}
