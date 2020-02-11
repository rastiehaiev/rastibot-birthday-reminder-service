package birthday.reminder.service.controller;

import birthday.reminder.service.model.BirthDayReminder;
import birthday.reminder.service.model.output.CreateBirthDayReminderResult;
import birthday.reminder.service.model.output.Result;
import birthday.reminder.service.service.BirthDayReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BirthDayReminderController {

    private final BirthDayReminderService birthDayReminderService;

    @PostMapping("/")
    public ResponseEntity<?> createBirthDayReminder(BirthDayReminder reminder) {
        Result<CreateBirthDayReminderResult> result = birthDayReminderService.create(reminder);
        return processResult(result);
    }

    private ResponseEntity<?> processResult(Result<?> result) {
        Assert.notNull(result, "result cannot be null.");
        if (result.getErrorPayload() != null) {
            return ResponseEntity.status(result.getStatusCode()).body(result.getErrorPayload());
        } else if (result.getPayload() != null) {
            return ResponseEntity.status(result.getStatusCode()).body(result.getPayload());
        }
        return ResponseEntity.ok().build();
    }
}
