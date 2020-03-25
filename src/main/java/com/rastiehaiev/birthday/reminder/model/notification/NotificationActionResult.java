package com.rastiehaiev.birthday.reminder.model.notification;

import com.rastiehaiev.birthday.reminder.model.Person;
import lombok.Data;

@Data
public class NotificationActionResult {

    private String callbackQueryId;
    private String actionPerformed;
    private Person person;
}
