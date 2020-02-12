package com.rastiehaiev.birthday.reminder.model;

import lombok.Data;

@Data
public class Notification {

    private Long id;
    private Long chatId;
    private Long reminderUserChatId;
    private String remindedUserFirstName;
    private String remindedUserLastName;
    private BirthDayReminderStrategy type;
    private int day;
    private int month;
    private Integer year;
}
