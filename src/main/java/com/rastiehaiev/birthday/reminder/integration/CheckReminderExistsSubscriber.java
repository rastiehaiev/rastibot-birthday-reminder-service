package com.rastiehaiev.birthday.reminder.integration;

import com.rastiehaiev.birthday.reminder.model.CheckReminderExistsRequest;
import com.rastiehaiev.birthday.reminder.model.CheckReminderExistsResult;
import com.rastiehaiev.birthday.reminder.model.ExistingReminder;
import com.rastiehaiev.birthday.reminder.service.BirthDayReminderService;
import com.sbrati.spring.boot.starter.gcp.pubsub.GcpPubSubSubscriber;
import com.sbrati.telegram.domain.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CheckReminderExistsSubscriber extends GcpPubSubSubscriber<CheckReminderExistsRequest> {

    private final BirthDayReminderService reminderService;
    private final CheckReminderExistsResultPublisher checkReminderExistsResultPublisher;

    public CheckReminderExistsSubscriber(BirthDayReminderService reminderService,
                                         CheckReminderExistsResultPublisher checkReminderExistsResultPublisher) {

        super(CheckReminderExistsRequest.class, "check-birthday-reminder-exists");
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
            Event<CheckReminderExistsResult> outEvent = new Event<>();
            outEvent.setPayload(result);
            outEvent.setTimestamp(System.currentTimeMillis());
            outEvent.setChatId(event.getChatId());
            checkReminderExistsResultPublisher.publish(outEvent);
        }
    }
}