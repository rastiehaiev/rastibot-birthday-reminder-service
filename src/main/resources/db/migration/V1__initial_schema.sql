CREATE TABLE birthday_reminder
(
    id                       SERIAL PRIMARY KEY,
    chat_id                  INT          NOT NULL,
    reminded_user_chat_id    INT          NOT NULL,
    reminded_user_first_name VARCHAR(255) NOT NULL,
    reminded_user_last_name  VARCHAR(255) DEFAULT NULL,
    next_birthday_timestamp  BIGINT       NOT NULL,
    last_updated             BIGINT       DEFAULT NULL,
    day                      INT          NOT NULL,
    month                    INT          NOT NULL,
    year                     INT          DEFAULT NULL,
    preferred_strategy       VARCHAR(255) DEFAULT NULL,
    disabled                 BOOLEAN      DEFAULT FALSE,
    deleted                  BOOLEAN      DEFAULT FALSE,
    UNIQUE (chat_id, reminded_user_chat_id)
);

CREATE INDEX birthday_reminder_next_birthday_timestamp_idx ON birthday_reminder (next_birthday_timestamp);

CREATE INDEX chat_id_reminded_user_chat_id_idx ON birthday_reminder (chat_id, reminded_user_chat_id);

ALTER TABLE birthday_reminder OWNER TO "rastibot-birthday-reminder-service";