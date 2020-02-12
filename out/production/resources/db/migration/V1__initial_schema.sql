CREATE TABLE birthday_reminder
(
    id                       SERIAL PRIMARY KEY,
    chat_id                  INT          NOT NULL,
    reminded_user_chat_id    INT          NOT NULL,
    reminded_user_first_name VARCHAR(255) NOT NULL,
    reminded_user_last_name  VARCHAR(255) DEFAULT NULL,
    next_birthday_timestamp  BIGINT       NOT NULL,
    day                      INT          NOT NULL,
    month                    INT          NOT NULL,
    year                     INT          NOT NULL,
    strategy                 VARCHAR(255) NOT NULL
);