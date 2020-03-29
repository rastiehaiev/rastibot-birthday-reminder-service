package com.rastiehaiev.birthday.reminder;

import com.rastiehaiev.birthday.reminder.model.BirthDayReminderStrategy;
import com.rastiehaiev.birthday.reminder.repository.BirthDayReminderRepository;
import com.rastiehaiev.birthday.reminder.repository.entity.BirthDayReminderEntity;
import com.rastiehaiev.birthday.reminder.service.BirthDayReminderProcessingService;
import com.rastiehaiev.birthday.reminder.service.NotificationSenderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
@ActiveProfiles("it")
@ContextConfiguration(initializers = {RastibotBirthDayReminderServiceApplicationIT.Initializer.class})
class RastibotBirthDayReminderServiceApplicationIT {

    @ClassRule
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>()
            .withDatabaseName("birthday_reminder_service")
            .withUsername("rastibot-birthday-reminder-service")
            .withPassword("password");

    @MockBean
    private Clock clock;
    @MockBean
    private NotificationSenderService notificationSenderService;

    @Autowired
    private BirthDayReminderRepository repository;
    @Autowired
    private BirthDayReminderProcessingService processorService;

    @Test
    public void shouldSendTwoWeeksBeforeNotificationForBirthdayReminderAndSetLastNotifiedDays() {
        when(clock.instant()).thenReturn(toUtc(2021, Month.APRIL, 28, 14, 40));
        BirthDayReminderEntity entity = createAndSaveEntity(2021, Month.MAY, 12);

        processorService.processBirthDayReminders();

        Optional<BirthDayReminderEntity> updated = repository.findById(entity.getId());
        assertTrue(updated.isPresent());
        assertEquals(14, updated.get().getLastNotifiedDays());
        verify(notificationSenderService, times(1)).sendNotifications(anyList());
    }

    @Test
    public void shouldSendWeekBeforeNotificationForBirthdayReminderAndSetLastNotifiedDays() {
        when(clock.instant()).thenReturn(toUtc(2021, Month.APRIL, 28, 14, 40));
        BirthDayReminderEntity entity = createAndSaveEntity(2021, Month.MAY, 5);

        processorService.processBirthDayReminders();

        Optional<BirthDayReminderEntity> updated = repository.findById(entity.getId());
        assertTrue(updated.isPresent());
        assertEquals(7, updated.get().getLastNotifiedDays());
        verify(notificationSenderService, times(1)).sendNotifications(anyList());
    }

    @Test
    public void shouldSendThreeDaysBeforeNotificationForBirthdayReminderAndSetLastNotifiedDays() {
        when(clock.instant()).thenReturn(toUtc(2021, Month.APRIL, 28, 14, 40));
        BirthDayReminderEntity entity = createAndSaveEntity(2021, Month.MAY, 1);

        processorService.processBirthDayReminders();

        Optional<BirthDayReminderEntity> updated = repository.findById(entity.getId());
        assertTrue(updated.isPresent());
        assertEquals(3, updated.get().getLastNotifiedDays());
        verify(notificationSenderService, times(1)).sendNotifications(anyList());
    }

    @Test
    public void shouldSendOneDayBeforeNotificationForBirthdayReminderAndSetLastNotifiedDays() {
        when(clock.instant()).thenReturn(toUtc(2021, Month.APRIL, 28, 14, 40));
        BirthDayReminderEntity entity = createAndSaveEntity(2021, Month.APRIL, 29);

        processorService.processBirthDayReminders();

        Optional<BirthDayReminderEntity> updated = repository.findById(entity.getId());
        assertTrue(updated.isPresent());
        assertEquals(1, updated.get().getLastNotifiedDays());
        verify(notificationSenderService, times(1)).sendNotifications(anyList());
    }

    @Test
    public void shouldSendOnADayNotificationForBirthdayReminderAndUnsetLastNotifiedDays() {
        when(clock.instant()).thenReturn(toUtc(2020, Month.MARCH, 29, 13, 25));
        BirthDayReminderEntity entity = createAndSaveEntity(2020, Month.MARCH, 29);

        processorService.processBirthDayReminders();

        Optional<BirthDayReminderEntity> updated = repository.findById(entity.getId());
        assertTrue(updated.isPresent());
        assertEquals(0, updated.get().getLastNotifiedDays());
        verify(notificationSenderService, times(1)).sendNotifications(anyList());
    }

    @Test
    public void shouldNotSendNotificationForBirthdayReminder_whenNoAppropriateStrategy() {
        when(clock.instant()).thenReturn(toUtc(2020, Month.MARCH, 28, 13, 25));
        createAndSaveEntity(2020, Month.MARCH, 30);

        processorService.processBirthDayReminders();
        verify(notificationSenderService, times(0)).sendNotifications(anyList());
    }

    @Test
    public void shouldNotSendNotificationForBirthdayReminder_whenPreferredStrategyDoesNotAllowIt() {
        when(clock.instant()).thenReturn(toUtc(2021, Month.APRIL, 28, 14, 40));
        createAndSaveEntity(2021, Month.MAY, 5, false, BirthDayReminderStrategy.THREE_DAYS_BEFORE);

        processorService.processBirthDayReminders();
        verify(notificationSenderService, times(0)).sendNotifications(anyList());
    }

    @Test
    public void shouldNotSendOnADayNotificationForBirthdayReminder_whenReminderDisabled() {
        when(clock.instant()).thenReturn(toUtc(2020, Month.MARCH, 29, 13, 25));
        createAndSaveEntity(2020, Month.MARCH, 29, true);

        processorService.processBirthDayReminders();

        verify(notificationSenderService, never()).sendNotifications(anyList());
    }

    @Test
    public void shouldNotSendNotificationTwice_whenLastUpdatedDateDoesNotAllowIt() {
        when(clock.instant()).thenReturn(toUtc(2020, Month.MARCH, 29, 13, 25));
        BirthDayReminderEntity entity = createAndSaveEntity(2020, Month.MARCH, 29);

        processorService.processBirthDayReminders();

        // send first time
        Optional<BirthDayReminderEntity> updated = repository.findById(entity.getId());
        assertTrue(updated.isPresent());
        assertEquals(0, updated.get().getLastNotifiedDays());
        verify(notificationSenderService, times(1)).sendNotifications(anyList());

        // do not send again
        processorService.processBirthDayReminders();
        verify(notificationSenderService, times(1)).sendNotifications(anyList());
    }

    @Test
    public void shouldNotSendNotificationTwice_whenNotificationAlreadySent() {
        when(clock.instant()).thenReturn(toUtc(2020, Month.MARCH, 29, 13, 25));
        BirthDayReminderEntity entity = createAndSaveEntity(2020, Month.MARCH, 29);

        processorService.processBirthDayReminders();

        // send first time
        Optional<BirthDayReminderEntity> updated = repository.findById(entity.getId());
        assertTrue(updated.isPresent());
        assertEquals(0, updated.get().getLastNotifiedDays());
        verify(notificationSenderService, times(1)).sendNotifications(anyList());

        // do not send in 3 hours
        when(clock.instant()).thenReturn(toUtc(2020, Month.MARCH, 29, 16, 25));
        processorService.processBirthDayReminders();
        verify(notificationSenderService, times(1)).sendNotifications(anyList());
    }

    @Test
    public void shouldUpdateExpiredReminderToBeUsedInTheNextYear() {
        when(clock.instant()).thenReturn(toUtc(2020, Month.MARCH, 29, 13, 25));
        BirthDayReminderEntity entity = createAndSaveEntity(2020, Month.MARCH, 28);
        long initialNextBirthdayTimestamp = entity.getNextBirthDayTimestamp();

        processorService.processBirthDayReminders();

        // send first time
        Optional<BirthDayReminderEntity> updated = repository.findById(entity.getId());
        assertTrue(updated.isPresent());
        assertNull(updated.get().getLastNotifiedDays());
        assertFalse(updated.get().isDisabled());
        assertTrue(initialNextBirthdayTimestamp < updated.get().getNextBirthDayTimestamp());
        assertEquals(toTimestamp(2021, Month.MARCH, 28), updated.get().getNextBirthDayTimestamp());
        verify(notificationSenderService, times(0)).sendNotifications(anyList());
    }

    @AfterEach
    public void removeAllFromDatabase() {
        repository.deleteAll();
    }

    private BirthDayReminderEntity createAndSaveEntity(int year, Month month, int day) {
        return createAndSaveEntity(year, month, day, false);
    }

    private BirthDayReminderEntity createAndSaveEntity(int year, Month month, int day, boolean disabled) {
        return createAndSaveEntity(year, month, day, disabled, null);
    }

    private BirthDayReminderEntity createAndSaveEntity(int year, Month month, int day, boolean disabled, BirthDayReminderStrategy preferred) {
        BirthDayReminderEntity entity = new BirthDayReminderEntity();
        entity.setChatId(11L);
        entity.setRemindedUserChatId(12L);
        entity.setRemindedUserFirstName("Johnny");
        entity.setRemindedUserLastName("Crocker");
        entity.setNextBirthDayTimestamp(toTimestamp(year, month, day));
        entity.setDay(day);
        entity.setMonth(month.getValue());
        entity.setDisabled(disabled);
        entity.setPreferredStrategy(preferred);
        BirthDayReminderEntity saved = repository.save(entity);
        entity.setId(saved.getId());
        return entity;
    }

    private Instant toUtc(int year, Month month, int day, int hours, int minutes) {
        return LocalDateTime.of(year, month, day, hours, minutes).toInstant(ZoneOffset.UTC);
    }

    private long toTimestamp(int year, Month month, int day) {
        return LocalDate.of(year, month, day).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    @AfterAll
    public static void shutDownContainer() {
        postgreSQLContainer.stop();
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            postgreSQLContainer.start();
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}