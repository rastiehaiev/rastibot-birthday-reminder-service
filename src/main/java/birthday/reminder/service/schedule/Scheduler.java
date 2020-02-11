package birthday.reminder.service.schedule;

import birthday.reminder.service.service.BirthDayReminderProcessingService;
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
