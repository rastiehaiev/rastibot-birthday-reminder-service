package com.rastiehaiev.birthday.reminder.model.notification;

import com.rastiehaiev.birthday.reminder.model.BirthDayReminderStrategy;
import com.rastiehaiev.birthday.reminder.model.Person;
import lombok.Data;

import java.util.List;

@Data
public class Notification {

    private Long id;
    private Long chatId;
    private Person person;
    private BirthDayReminderStrategy type;
    private int day;
    private int month;
    private Integer year;
    private List<String> actions;
}
