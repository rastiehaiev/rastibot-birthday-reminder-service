package com.rastiehaiev.birthday.reminder.exception;

public class ReminderAlreadyExistsException extends ReminderServiceException {

    public ReminderAlreadyExistsException(String message) {
        super(message);
    }
}
