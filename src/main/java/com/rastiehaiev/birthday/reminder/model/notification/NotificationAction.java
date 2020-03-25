package com.rastiehaiev.birthday.reminder.model.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum NotificationAction {

    DO_NOT_NOTIFY_ANYMORE(-1, "dnna"),
    DO_NOT_NOTIFY_THIS_YEAR(0, "dnnty"),
    NOTIFY_WEEK_BEFORE(7, "nwb"),
    NOTIFY_THREE_DAYS_BEFORE(3, "ntdb"),
    NOTIFY_A_DAY_BEFORE(1, "nadb"),
    NOTIFY_AT_THE_DAY(0, "natd");

    private final int supportedDaysBefore;
    private final String abbreviation;

    public static NotificationAction from(String value) {
        return Stream.of(NotificationAction.values())
                .filter(it -> it.getAbbreviation().equals(value))
                .findFirst()
                .orElse(null);
    }
}
