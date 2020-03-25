package com.rastiehaiev.birthday.reminder.model.notification;

import lombok.Data;

@Data
public class NotificationActionRequest {

    private long notificationId;
    private String callbackQueryId;
    private String action;
}
