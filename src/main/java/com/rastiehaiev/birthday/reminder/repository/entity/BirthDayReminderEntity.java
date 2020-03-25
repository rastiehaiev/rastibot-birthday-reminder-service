package com.rastiehaiev.birthday.reminder.repository.entity;

import com.rastiehaiev.birthday.reminder.model.BirthDayReminderStrategy;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "birthday_reminder")
public class BirthDayReminderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "chat_id")
    private long chatId;

    @Column(name = "reminded_user_chat_id")
    private long remindedUserChatId;

    @Column(name = "reminded_user_first_name", nullable = false)
    private String remindedUserFirstName;

    @Column(name = "reminded_user_last_name")
    private String remindedUserLastName;

    @Column(name = "next_birthday_timestamp")
    private long nextBirthDayTimestamp;

    @Column(name = "day")
    private int day;
    @Column(name = "month")
    private int month;
    @Column(name = "year")
    private Integer year;

    @Column(name = "preferred_strategy")
    @Enumerated(value = EnumType.STRING)
    private BirthDayReminderStrategy preferredStrategy;

    @Column(name = "last_updated")
    private Long lastUpdated;

    @Column(name = "disabled")
    private boolean disabled;

    @Column(name = "deleted")
    private boolean deleted;
}
