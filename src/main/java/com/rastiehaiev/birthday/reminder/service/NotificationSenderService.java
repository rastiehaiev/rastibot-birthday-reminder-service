package com.rastiehaiev.birthday.reminder.service;

import com.rastiehaiev.birthday.reminder.integration.BirthdayReminderNotificationPublisher;
import com.rastiehaiev.birthday.reminder.model.notification.Notification;
import com.sbrati.telegram.domain.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSenderService {

    private final BirthdayReminderNotificationPublisher publisher;

    public void sendNotifications(List<Notification> notifications) {
        CollectionUtils.emptyIfNull(notifications)
                .stream()
                .map(this::wrapWithEvent)
                .forEach(publisher::publish);
    }

    private Event<Notification> wrapWithEvent(Notification notification) {
        Event<Notification> event = new Event<>();
        event.setPayload(notification);
        event.setChatId(notification.getChatId());
        event.setTimestamp(System.currentTimeMillis());
        event.setGlobal(true);
        return event;
    }
}
