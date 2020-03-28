package com.rastiehaiev.birthday.reminder.integration;

import com.rastiehaiev.birthday.reminder.exception.ReminderAlreadyExistsException;
import com.rastiehaiev.birthday.reminder.model.BirthDayReminder;
import com.rastiehaiev.birthday.reminder.model.CreateReminderRequest;
import com.rastiehaiev.birthday.reminder.model.CreateReminderResult;
import com.rastiehaiev.birthday.reminder.model.output.CreateBirthDayReminderSuccess;
import com.rastiehaiev.birthday.reminder.service.BirthDayReminderService;
import com.rastiehaiev.birthday.reminder.service.EventCreationService;
import com.sbrati.spring.boot.starter.gcp.pubsub.GcpPubSubSubscriber;
import com.sbrati.telegram.domain.Event;
import com.sbrati.telegram.domain.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CreateReminderSubscriber extends GcpPubSubSubscriber<CreateReminderRequest> {

    private final BirthDayReminderService reminderService;
    private final EventCreationService eventCreationService;
    private final CreateReminderResultPublisher createReminderResultPublisher;

    public CreateReminderSubscriber(BirthDayReminderService reminderService,
                                    EventCreationService eventCreationService,
                                    CreateReminderResultPublisher createReminderResultPublisher) {

        super(CreateReminderRequest.class, "create-birthday-reminder");
        this.reminderService = reminderService;
        this.eventCreationService = eventCreationService;
        this.createReminderResultPublisher = createReminderResultPublisher;
    }

    @Override
    public void process(Event<CreateReminderRequest> event) {
        log.info("Processing event {}", event);
        if (event != null && event.getPayload() != null) {
            Event<CreateReminderResult> outEvent = eventCreationService.create(event.getChatId(), null);
            try {
                CreateBirthDayReminderSuccess success = reminderService.create(toReminder(event.getPayload()));
                outEvent.setPayload(getCreateReminderResult(success));
            } catch (ReminderAlreadyExistsException e) {
                outEvent.setStatusCode(StatusCode.CONFLICT);
                outEvent.setStatusMessage("birthdayreminder.conflict.reminder.not.created");
                outEvent.setPayload(new CreateReminderResult());
            }
            createReminderResultPublisher.publish(outEvent);
        }
    }

    private CreateReminderResult getCreateReminderResult(CreateBirthDayReminderSuccess success) {
        CreateReminderResult result = new CreateReminderResult();
        result.setNextBirthdayTimestamp(success.getNextBirthDayTimestamp());
        return result;
    }

    private BirthDayReminder toReminder(CreateReminderRequest request) {
        BirthDayReminder reminder = new BirthDayReminder();
        reminder.setChatId(request.getChatId());
        reminder.setPerson(request.getPerson());
        reminder.setMonth(request.getBirthday().getMonth().getValue());
        reminder.setDay(request.getBirthday().getDay());
        reminder.setYear(request.getBirthday().getYear());
        reminder.setOverride(request.isOverride());
        return reminder;
    }
}
