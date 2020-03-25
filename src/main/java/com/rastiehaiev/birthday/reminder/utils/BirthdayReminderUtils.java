package com.rastiehaiev.birthday.reminder.utils;

import com.rastiehaiev.birthday.reminder.model.Person;
import com.rastiehaiev.birthday.reminder.repository.entity.BirthDayReminderEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BirthdayReminderUtils {

    public static Person toPerson(BirthDayReminderEntity reminder) {
        Person person = new Person();
        person.setChatId(reminder.getRemindedUserChatId());
        person.setFirstName(reminder.getRemindedUserFirstName());
        person.setLastName(reminder.getRemindedUserLastName());
        return person;
    }
}
