package com.rastiehaiev.birthday.reminder.model;

import lombok.Data;

@Data
public class DeleteBirthdayReminderResult {

    private OperationStatus status;
    private long messageId;
}
