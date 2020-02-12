package com.rastiehaiev.birthday.reminder.service;

import com.rastiehaiev.birthday.reminder.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class NotificationSenderService {

    public void sendNotifications(List<Notification> notifications) {
        // This is temporary NOOP notifications sender. Will be probably replaced with Kafka
        CollectionUtils.emptyIfNull(notifications)
                .forEach(notification -> log.info("Sending notification {}.", notification));
    }
}
