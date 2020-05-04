package com.rastiehaiev.birthday.reminder.model;

import lombok.Data;

@Data
public class ListBirthdayRemindersRequest {

    private long chatId;
    private ListBirthdayRemindersKind kind;
}
