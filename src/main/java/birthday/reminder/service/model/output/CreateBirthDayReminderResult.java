package birthday.reminder.service.model.output;

import lombok.Data;

@Data
public class CreateBirthDayReminderResult {

    private final long nextBirthDayTimestamp;
}
