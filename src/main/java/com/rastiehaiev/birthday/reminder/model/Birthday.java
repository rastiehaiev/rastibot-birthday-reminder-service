package com.rastiehaiev.birthday.reminder.model;

import lombok.Data;

import java.time.Month;

@Data
public class Birthday {

    private int day;
    private Month month;
    private Integer year;
}
