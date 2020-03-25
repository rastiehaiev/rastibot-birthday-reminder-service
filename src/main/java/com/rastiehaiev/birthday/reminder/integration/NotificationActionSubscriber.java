package com.rastiehaiev.birthday.reminder.integration;

import com.rastiehaiev.birthday.reminder.model.Person;
import com.rastiehaiev.birthday.reminder.model.notification.NotificationActionRequest;
import com.rastiehaiev.birthday.reminder.model.notification.NotificationActionResult;
import com.rastiehaiev.birthday.reminder.service.BirthDayReminderService;
import com.sbrati.spring.boot.starter.gcp.pubsub.GcpPubSubSubscriber;
import com.sbrati.telegram.domain.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Slf4j
@Component
public class NotificationActionSubscriber extends GcpPubSubSubscriber<NotificationActionRequest> {

    private final BirthDayReminderService reminderService;
    private final NotificationActionResultPublisher publisher;

    public NotificationActionSubscriber(BirthDayReminderService reminderService, NotificationActionResultPublisher publisher) {
        super(NotificationActionRequest.class, "birthday-reminder-notification-action");
        this.reminderService = reminderService;
        this.publisher = publisher;
    }

    @Override
    @Transactional
    public void process(Event<NotificationActionRequest> event) {
        log.info("Received an event: {}.", event);
        NotificationActionRequest request = event.getPayload();
        Person affectedPerson = reminderService.reactOnNotificationAction(request);
        if (affectedPerson != null) {
            NotificationActionResult result = new NotificationActionResult();
            result.setCallbackQueryId(request.getCallbackQueryId());
            result.setActionPerformed(request.getAction());
            result.setPerson(affectedPerson);

            Event<NotificationActionResult> outEvent = createEvent(event, result);
            log.info("Result event: {}", outEvent);
            publisher.publish(outEvent);
        }
    }

    private Event<NotificationActionResult> createEvent(Event<NotificationActionRequest> event, NotificationActionResult result) {
        Event<NotificationActionResult> outEvent = new Event<>();
        outEvent.setPayload(result);
        outEvent.setTimestamp(System.currentTimeMillis());
        outEvent.setGlobal(true);
        outEvent.setChatId(event.getChatId());
        return outEvent;
    }
}
