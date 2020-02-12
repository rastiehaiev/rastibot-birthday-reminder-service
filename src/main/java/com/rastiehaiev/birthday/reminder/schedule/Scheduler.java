package com.rastiehaiev.birthday.reminder.schedule;

import com.rastiehaiev.birthday.reminder.service.BirthDayReminderProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

@RequiredArgsConstructor
public class Scheduler {

    private final BirthDayReminderProcessingService birthDayReminderProcessingService;

    @Scheduled(cron = "${birthday-reminder-service.schedule.cron}")
    public void processBirthDayReminders() {
        birthDayReminderProcessingService.processBirthDayReminders();
    }
}
