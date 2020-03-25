package com.rastiehaiev.birthday.reminder.exception;

import com.rastiehaiev.birthday.reminder.model.BirthDayReminder;
import lombok.Getter;

public class ReminderAlreadyExistsException extends ConflictException {

    @Getter
    private final BirthDayReminder existing;

    public ReminderAlreadyExistsException(String message, BirthDayReminder existing) {
        super(message);
        this.existing = existing;
    }
}
