# birthday-reminder-service

This service is a part of a future Telegram bot which will send notifications about upcoming birthdays.

Currently, the service supports creation of birthday reminders via HTTP request. Example cUrl:

```sh
$ curl --header "Content-Type: application/json" \
    --request POST \
    --data '{"chatId":1,"day":19,"month":2,"year":1993,"person":{"chatId":2,"firstName":"Name","lastName":"Surname"}}' \
    http://localhost:9292/reminder
```

Scheduled service is configured to process created reminders on the daily basis. There are several 'stages' for them:
- `TWO_WEEKS_BEFORE`
- `A_WEEK_BEFORE`
- `THREE_DAYS_BEFORE`
- `A_DAY_BEFORE`
- `ON_A_DAY`

Thus, user will be notified about upcoming event several times: firstly, two weeks before the event, then a week before, then three days before, a day before and actually on a day.

The resulted notification payload looks like this:
```json
{
  "id": 1,
  "chatId": 1,
  "remindedUserChatId": 1,
  "remindedUserFirstName": "First Name",
  "remindedUserLastName": "Last Name",
  "stage": "THREE_DAYS_BEFORE"
}
```
Consuming service will then decide how to handle this message and send reminder to user in a most appropriate way.

# TODO
1. Write more JUnit/Integration tests.
2. Add possibility to change current reminder stage via HTTP.
3. Add possibility to disable notifications or even delete the reminder.
4. Send notifications to Kafka, so another service can read them and process.
5. Move scheduler to the separate service.
