package com.rastiehaiev.birthday.reminder.repository;

import com.rastiehaiev.birthday.reminder.repository.entity.BirthDayReminderEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BirthDayReminderRepository extends CrudRepository<BirthDayReminderEntity, Long> {

    BirthDayReminderEntity findByChatIdAndRemindedUserChatId(long chatId, long remindedUserChatId);

    BirthDayReminderEntity findByChatIdAndRemindedUserChatIdAndDeletedFalse(long chatId, long remindedUserChatId);

    long countAllByDeletedFalse();

    @Query(value = "SELECT * FROM birthday_reminder WHERE next_birthday_timestamp < :upcomingTimestamp AND deleted = false " +
            "AND (last_updated is NULL OR last_updated < :lastUpdatedTimestamp)", nativeQuery = true)
    List<BirthDayReminderEntity> findUpcoming(@Param("upcomingTimestamp") long upcomingTimestamp,
                                              @Param("lastUpdatedTimestamp") long lastUpdatedGap,
                                              Pageable pageable);

    @Modifying
    @Query(value = "UPDATE birthday_reminder SET last_notified_days = :last_notified_days WHERE id = :id")
    void updateLastNotifiedDays(@Param("id") long id, @Param("last_notified_days") Integer lastNotifiedDays);

    @Query(value = "SELECT * FROM birthday_reminder WHERE chat_id = :chatId AND deleted = false " +
            "ORDER BY next_birthday_timestamp LIMIT 1", nativeQuery = true)
    BirthDayReminderEntity findNearest(@Param("chatId") long chatId);

    @Query(value = "SELECT * FROM birthday_reminder WHERE chat_id = :chatId AND deleted = false " +
            "ORDER BY next_birthday_timestamp LIMIT 3", nativeQuery = true)
    List<BirthDayReminderEntity> findThreeNearest(@Param("chatId") long chatId);

    List<BirthDayReminderEntity> findAllByChatIdAndDeletedFalseOrderByNextBirthDayTimestamp(long chatId, Pageable pageable);

    @Query(value = "SELECT * FROM birthday_reminder WHERE chat_id = :chatId AND month = :month AND deleted = false " +
            "ORDER BY next_birthday_timestamp LIMIT 10", nativeQuery = true)
    List<BirthDayReminderEntity> findByMonth(@Param("chatId") long chatId, @Param("month") int month);
}
