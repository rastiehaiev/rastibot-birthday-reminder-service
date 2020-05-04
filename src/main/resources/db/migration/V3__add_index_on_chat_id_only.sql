DROP INDEX chat_id_reminded_user_chat_id_idx;

CREATE INDEX chat_id_idx ON birthday_reminder (chat_id);
