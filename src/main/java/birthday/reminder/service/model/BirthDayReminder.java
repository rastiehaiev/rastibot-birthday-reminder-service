package birthday.reminder.service.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BirthDayReminder {

    private long chatId;

    private int day;
    private int month;
    private int year;

    @NotNull
    private Person person;
}
