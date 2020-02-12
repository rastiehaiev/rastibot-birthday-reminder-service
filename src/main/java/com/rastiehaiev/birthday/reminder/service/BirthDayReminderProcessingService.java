package com.rastiehaiev.birthday.reminder.service;

import com.rastiehaiev.birthday.reminder.model.Notification;
import com.rastiehaiev.birthday.reminder.processor.ReminderStrategyProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BirthDayReminderProcessingService {

    private final List<ReminderStrategyProcessor> processors;
    private final NotificationSenderService notificationSenderService;

    public void processBirthDayReminders() {
        for (ReminderStrategyProcessor processor : processors) {
            log.info("Processing birthday reminders with '{}' strategy.", processor.applicableStrategy());
            boolean nextBatchAvailable = true;
            int currentBatch = 1;
            while (nextBatchAvailable) {
                List<Notification> notifications = processor.process();
                if (CollectionUtils.isEmpty(notifications)) {
                    nextBatchAvailable = false;
                } else {
                    log.info("[{}] Batch number: {}.", processor.applicableStrategy(), currentBatch);
                    notificationSenderService.sendNotifications(notifications);
                    currentBatch++;
                }
            }
        }
    }
}
