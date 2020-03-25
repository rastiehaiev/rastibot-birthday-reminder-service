package com.rastiehaiev.birthday.reminder.model.output;

import com.rastiehaiev.birthday.reminder.model.BirthDayReminder;
import lombok.Data;

@Data
public class CreateBirthDayReminderConflict {

    private final BirthDayReminder existing;
    private final BirthDayReminder requested;
}
