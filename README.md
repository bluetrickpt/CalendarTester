# Calendar Tester v1.0

## Description

This small app serves as an example of how one can retrieve the events from the installed calendars.

Currently retrieves, for each event:

            CalendarContract.Events._ID,
            CalendarContract.Events.CALENDAR_ID,
            CalendarContract.Events.EVENT_LOCATION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.EVENT_TIMEZONE,
            CalendarContract.Events.EVENT_END_TIMEZONE,
            CalendarContract.Events.DURATION,
            CalendarContract.Events.ALL_DAY,
            CalendarContract.Events.RRULE,
            CalendarContract.Events.RDATE,
            CalendarContract.Events.AVAILABILITY,
            CalendarContract.Events.GUESTS_CAN_MODIFY,
            CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS,
            CalendarContract.Events.GUESTS_CAN_SEE_GUESTS
This is not all the information one can retrieve from the event (this was not the objective).

This application requires READ_CALENDAR permission and it will ask for this permission at runtime if device sdk >= android Marshmallow (api lvl 23), or at install time for older devices.

## Small output example

            Event 1 of 1
            _id: 1
            calendar_id: 4
            eventLocation: Office
            dtstart: 1500310800000
            dtend: null
            eventTimezone: Europe/Lisbon
            eventEndTimezone: null
            duration: P3600S
            allDay: 0
            rrule: FREQ=WEEKLY;WKST=SU
            rdate: null
            availability: 0
            guestsCanModify: 0
            guestsCanInviteOthers: 1
            guestsCanSeeGuests: 1
