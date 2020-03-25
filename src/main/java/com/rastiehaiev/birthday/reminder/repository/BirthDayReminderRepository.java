package com.rastiehaiev.birthday.reminder.repository;

import com.rastiehaiev.birthday.reminder.repository.entity.BirthDayReminderEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BirthDayReminderRepository extends CrudRepository<BirthDayReminderEntity, Long> {

    BirthDayReminderEntity findByChatIdAndRemindedUserChatId(long chatId, long remindedUserChatId);

    @Query(value = "SELECT * FROM birthday_reminder WHERE next_birthday_timestamp < :upcomingTimestamp " +
            "AND (last_updated IS NULL OR last_updated > :lastUpdatedMark) AND deleted = false FOR UPDATE",
            nativeQuery = true)
    List<BirthDayReminderEntity> findUpcoming(@Param("upcomingTimestamp") long upcomingTimestamp,
                                              @Param("lastUpdatedMark") long lastUpdatedMark,
                                              Pageable pageable);
}
