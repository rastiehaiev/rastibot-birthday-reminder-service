package com.rastiehaiev.birthday.reminder.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ConfigurationProperties(prefix = "birthday-reminder-service.schedule")
public class RastibotBirthDayReminderServiceScheduleProperties {

    @NotNull
    @Min(10)
    @Max(1000)
    private Integer batchSize;

    @NotNull
    @Min(1)
    @Max(12)
    private Integer lastUpdatedReminderHours;

    private String zone;
}
