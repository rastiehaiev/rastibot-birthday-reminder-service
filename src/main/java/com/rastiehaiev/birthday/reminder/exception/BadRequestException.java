package com.rastiehaiev.birthday.reminder.exception;

public abstract class BadRequestException extends ReminderServiceException {

    public BadRequestException(String message) {
        super(message);
    }
}
