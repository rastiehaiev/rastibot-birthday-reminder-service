package com.rastiehaiev.birthday.reminder.integration;

import com.rastiehaiev.birthday.reminder.model.Person;
import com.rastiehaiev.birthday.reminder.model.notification.NotificationActionRequest;
import com.rastiehaiev.birthday.reminder.model.notification.NotificationActionResult;
import com.rastiehaiev.birthday.reminder.service.BirthDayReminderService;
import com.rastiehaiev.birthday.reminder.service.EventCreationService;
import com.sbrati.spring.boot.starter.gcp.pubsub.GcpPubSubSubscriber;
import com.sbrati.telegram.domain.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Slf4j
@Component
public class NotificationActionSubscriber extends GcpPubSubSubscriber<NotificationActionRequest> {

    private final BirthDayReminderService reminderService;
    private final EventCreationService eventCreationService;
    private final NotificationActionResultPublisher publisher;

    public NotificationActionSubscriber(BirthDayReminderService reminderService,
                                        EventCreationService eventCreationService,
                                        NotificationActionResultPublisher publisher) {

        super(NotificationActionRequest.class, "birthday-reminder-notification-action");
        this.reminderService = reminderService;
        this.eventCreationService = eventCreationService;
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

            Event<NotificationActionResult> outEvent = eventCreationService.createGlobal(event.getChatId(), result);
            log.info("Notification action result event: {}", outEvent);
            publisher.publish(outEvent);
        }
    }
}
