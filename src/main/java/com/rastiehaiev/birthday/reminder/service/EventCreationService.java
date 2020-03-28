package com.rastiehaiev.birthday.reminder.service;

import com.sbrati.telegram.domain.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
@RequiredArgsConstructor
public class EventCreationService {

    private final Clock clock;

    public <T> Event<T> create(long chatId, T payload) {
        return create(chatId, payload, false);
    }

    public <T> Event<T> createGlobal(long chatId, T payload) {
        return create(chatId, payload, true);
    }

    private <T> Event<T> create(long chatId, T payload, boolean global) {
        Event<T> event = new Event<>();
        event.setPayload(payload);
        event.setChatId(chatId);
        event.setTimestamp(clock.millis());
        event.setGlobal(global);
        return event;
    }
}
