package com.rastiehaiev.birthday.reminder.model;

import lombok.Data;

@Data
public class BirthDayReminder {

    private Long chatId;
    private int day;
    private int month;
    private Integer year;
    private Person person;
    private boolean override;
}
