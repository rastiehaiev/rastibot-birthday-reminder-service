package com.rastiehaiev.birthday.reminder.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Person {

    @NotNull
    private Long chatId;
    @NotNull
    private String firstName;
    private String lastName;
}
