package com.rastiehaiev.birthday.reminder.integration;

import com.rastiehaiev.birthday.reminder.model.CheckReminderExistsRequest;
import com.rastiehaiev.birthday.reminder.model.CheckReminderExistsResult;
import com.rastiehaiev.birthday.reminder.model.ExistingReminder;
import com.rastiehaiev.birthday.reminder.service.BirthDayReminderService;
import com.rastiehaiev.birthday.reminder.service.EventCreationService;
import com.sbrati.spring.boot.starter.gcp.pubsub.GcpPubSubSubscriber;
import com.sbrati.telegram.domain.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CheckReminderExistsSubscriber extends GcpPubSubSubscriber<CheckReminderExistsRequest> {

    private final BirthDayReminderService reminderService;
    private final EventCreationService eventCreationService;
    private final CheckReminderExistsResultPublisher checkReminderExistsResultPublisher;

    public CheckReminderExistsSubscriber(EventCreationService eventCreationService,
                                         BirthDayReminderService reminderService,
                                         CheckReminderExistsResultPublisher checkReminderExistsResultPublisher) {

        super(CheckReminderExistsRequest.class, "check-birthday-reminder-exists");
        this.eventCreationService = eventCreationService;
        this.reminderService = reminderService;
        this.checkReminderExistsResultPublisher = checkReminderExistsResultPublisher;
    }

    @Override
    public void process(Event<CheckReminderExistsRequest> event) {
        log.info("Processing event {}", event);
        CheckReminderExistsRequest request = event.getPayload();
        if (request != null) {
            ExistingReminder existing = reminderService.findExisting(request.getChatId(), request.getReminderUserChatId());
            CheckReminderExistsResult result = new CheckReminderExistsResult();
            result.setExistingReminder(existing);

            Event<CheckReminderExistsResult> outEvent = eventCreationService.create(event.getChatId(), result);
            checkReminderExistsResultPublisher.publish(outEvent);
        }
    }
}