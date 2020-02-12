package com.rastiehaiev.birthday.reminder.exception;

public abstract class ReminderServiceException extends RuntimeException {

    public ReminderServiceException(String message) {
        super(message);
    }
}
