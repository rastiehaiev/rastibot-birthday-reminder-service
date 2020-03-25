package com.rastiehaiev.birthday.reminder.exception;

public abstract class ConflictException extends ReminderServiceException {

    public ConflictException(String message) {
        super(message);
    }
}
