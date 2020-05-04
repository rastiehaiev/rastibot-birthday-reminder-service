package com.rastiehaiev.birthday.reminder.model;

import lombok.Data;

@Data
public class DeleteBirthdayReminderRequest {

    private long reminderId;
    private long messageId;
}
