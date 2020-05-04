package com.rastiehaiev.birthday.reminder.controller;

import com.rastiehaiev.birthday.reminder.model.BirthdayStatistics;
import com.rastiehaiev.birthday.reminder.service.BirthDayReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reminders")
@RequiredArgsConstructor
public class BirthdayReminderController {

    private final BirthDayReminderService reminderService;

    @GetMapping(value = "/count")
    public Long countBirthdayReminders() {
        return reminderService.countAll();
    }

    @GetMapping(value = "/stats")
    public BirthdayStatistics statistics() {
        BirthdayStatistics birthdayStatistics = new BirthdayStatistics();
        birthdayStatistics.setTotal(reminderService.countAll());
        birthdayStatistics.setActive(reminderService.countNotDeleted());
        return birthdayStatistics;
    }
}
