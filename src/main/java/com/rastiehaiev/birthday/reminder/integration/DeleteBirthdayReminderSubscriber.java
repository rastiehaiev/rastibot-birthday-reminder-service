package com.rastiehaiev.birthday.reminder.integration;

import com.rastiehaiev.birthday.reminder.model.DeleteBirthdayReminderRequest;
import com.rastiehaiev.birthday.reminder.model.DeleteBirthdayReminderResult;
import com.rastiehaiev.birthday.reminder.model.OperationStatus;
import com.rastiehaiev.birthday.reminder.service.BirthDayReminderService;
import com.rastiehaiev.birthday.reminder.service.EventCreationService;
import com.sbrati.spring.boot.starter.gcp.pubsub.GcpPubSubSubscriber;
import com.sbrati.telegram.domain.Event;

public class DeleteBirthdayReminderSubscriber extends GcpPubSubSubscriber<DeleteBirthdayReminderRequest> {

    private final BirthDayReminderService reminderService;
    private final DeleteBirthdayReminderResultPublisher publisher;
    private final EventCreationService eventCreationService;

    public DeleteBirthdayReminderSubscriber(BirthDayReminderService reminderService, DeleteBirthdayReminderResultPublisher publisher, EventCreationService eventCreationService) {
        super(DeleteBirthdayReminderRequest.class, "delete-birthday-reminder");
        this.reminderService = reminderService;
        this.publisher = publisher;
        this.eventCreationService = eventCreationService;
    }

    @Override
    public void process(Event<DeleteBirthdayReminderRequest> event) {
        DeleteBirthdayReminderRequest request = event.getPayload();
        reminderService.markAsDeleted(request.getReminderId());

        DeleteBirthdayReminderResult result = new DeleteBirthdayReminderResult();
        result.setStatus(OperationStatus.SUCCESS);
        result.setMessageId(request.getMessageId());

        Event<DeleteBirthdayReminderResult> resultEvent = eventCreationService.create(event.getChatId(), result);
        resultEvent.setGlobal(true);
        publisher.publish(resultEvent);
    }
}
