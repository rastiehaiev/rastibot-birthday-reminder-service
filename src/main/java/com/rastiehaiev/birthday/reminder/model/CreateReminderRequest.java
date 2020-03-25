package com.rastiehaiev.birthday.reminder.model;

import lombok.Data;

@Data
public class CreateReminderRequest {

    private long chatId;
    private Person person;
    private Birthday birthday;
    private boolean override;
}
