package com.rastiehaiev.birthday.reminder.controller;

import com.rastiehaiev.birthday.reminder.exception.ReminderAlreadyExistsException;
import com.rastiehaiev.birthday.reminder.model.BirthDayReminder;
import com.rastiehaiev.birthday.reminder.model.output.CreateBirthDayReminderResult;
import com.rastiehaiev.birthday.reminder.model.output.ErrorPayload;
import com.rastiehaiev.birthday.reminder.service.BirthDayReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class BirthDayReminderController {

    private final BirthDayReminderService birthDayReminderService;

    @PostMapping("/reminder")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateBirthDayReminderResult createBirthDayReminder(@Valid @RequestBody BirthDayReminder reminder) {
        CreateBirthDayReminderResult result = birthDayReminderService.create(reminder);
        Assert.notNull(result, "result cannot be null.");
        return result;
    }

    @ExceptionHandler({ReminderAlreadyExistsException.class})
    public ResponseEntity<?> handleException(ReminderAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorPayload(e.getMessage()));
    }
}
