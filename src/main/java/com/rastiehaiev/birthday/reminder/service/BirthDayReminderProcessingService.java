package com.rastiehaiev.birthday.reminder.service;

import com.rastiehaiev.birthday.reminder.model.Notification;
import com.rastiehaiev.birthday.reminder.processor.BirthDayReminderProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BirthDayReminderProcessingService {

    private final BirthDayReminderProcessor processor;
    private final NotificationSenderService notificationSenderService;

    public void processBirthDayReminders() {
        log.info("Started processing birthday reminders.");
        boolean nextBatchAvailable = true;
        int currentBatch = 1;
        while (nextBatchAvailable) {
            List<Notification> notifications = processor.process();
            if (CollectionUtils.isEmpty(notifications)) {
                nextBatchAvailable = false;
            } else {
                log.info("Batch number: {}.", currentBatch);
                notificationSenderService.sendNotifications(notifications);
                currentBatch++;
            }
        }
        log.info("Processing of birthday reminders has finished.");
    }
}
