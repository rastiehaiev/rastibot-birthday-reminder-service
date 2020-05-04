package com.rastiehaiev.birthday.reminder.model;

import lombok.Data;

@Data
public class BirthdayReminderData {

    private long id;
    private Person person;
    private Birthday birthday;
}
