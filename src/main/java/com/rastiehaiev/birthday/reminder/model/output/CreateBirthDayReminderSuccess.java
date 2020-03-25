package com.rastiehaiev.birthday.reminder.model.output;

import com.rastiehaiev.birthday.reminder.model.BirthDayReminder;
import lombok.Data;

@Data
public class CreateBirthDayReminderSuccess {

    private final long nextBirthDayTimestamp;
    private final BirthDayReminder birthDayReminder;
}
