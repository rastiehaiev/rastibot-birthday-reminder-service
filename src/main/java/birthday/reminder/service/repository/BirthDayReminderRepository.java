package birthday.reminder.service.repository;

import birthday.reminder.service.entity.BirthDayReminderEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BirthDayReminderRepository extends CrudRepository<BirthDayReminderEntity, Long> {

    BirthDayReminderEntity findByChatIdAndRemindedUserChatId(long chatId, long remindedUserChatId);

    @Query(value = "SELECT * FROM birthday_reminder WHERE strategy = :strategy AND next_birthday_timestamp = :next_birthday_timestamp FOR UPDATE", nativeQuery = true)
    List<BirthDayReminderEntity> findApplicableByStrategyAndNextBirthDayTimestamp(@Param("strategy") String strategy,
                                                                                  @Param("next_birthday_timestamp") long nextBirthdayTimestamp,
                                                                                  Pageable pageable);
}
