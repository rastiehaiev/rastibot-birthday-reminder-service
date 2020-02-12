package birthday.reminder.service.model;

import lombok.Data;

@Data
public class Notification {

    private Long id;
    private Long chatId;
    private Long reminderUserChatId;
    private String remindedUserFirstName;
    private String remindedUserLastName;
    private BirthDayReminderStrategy strategy;
}
