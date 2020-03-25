package com.rastiehaiev.birthday.reminder.model;

import lombok.Data;

@Data
public class CheckReminderExistsRequest {

    private long chatId;
    private long reminderUserChatId;
}
