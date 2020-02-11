package birthday.reminder.service.model;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class BirthDayReminder {

    @NotNull
    @Min(1)
    private Long chatId;
    @NotNull
    @Min(1)
    private int day;
    @NotNull
    @Min(1)
    private int month;
    @NotNull
    @Min(1900)
    private int year;
    @NotNull
    @Valid
    private Person person;
}
