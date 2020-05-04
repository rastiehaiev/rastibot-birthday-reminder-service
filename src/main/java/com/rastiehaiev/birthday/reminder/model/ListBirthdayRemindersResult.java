package com.rastiehaiev.birthday.reminder.model;

import lombok.Data;

import java.util.List;

@Data
public class ListBirthdayRemindersResult {

    private List<BirthdayReminderData> results;
}
