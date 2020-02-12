package com.rastiehaiev.birthday.reminder.model.output;

import lombok.Data;

@Data
public class CreateBirthDayReminderResult {

    private final long nextBirthDayTimestamp;
}
