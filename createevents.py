#v2
# File for the creation of events and adding them to the users calendar
from googleapiclient.discovery import build
from google_auth_oauthlib.flow import InstalledAppFlow
from google.auth.transport.requests import Request
import datetime
from datetime import timedelta
import os.path
import pickle

from findfreeslots import find_free_slots, authenticate, next_10am_local, fetch_events, parse_event_time, day_window_boundaries

# Constants
SCOPES = ['https://www.googleapis.com/auth/calendar']
TOKEN_PATH = 'token.pickle'
CREDENTIALS_FILE = 'credentials.json'
CALENDAR_ID = 'hackathon'  # Replace with actual calendar ID if needed
SEARCH_WINDOW_HOURS = 12
DAYS_AHEAD = 7
WORK_START_HOUR = 10
WORK_END_HOUR = 22

def create_event(service, summary, description, start_time, end_time):
    event = {
        'summary': summary,
        'description': description,
        'start': {
            'dateTime': start_time.isoformat(),
            'timeZone': 'America/Los_Angeles'
        },
        'end': {
            'dateTime': end_time.isoformat(),
            'timeZone': 'America/Los_Angeles'
        },
    }
    return service.events().insert(calendarId=CALENDAR_ID, body=event).execute()

def find_first_available_week_slot(events, start, duration_minutes, days=DAYS_AHEAD):
    week_windows = day_window_boundaries(start, days)
    for window_start, window_end in week_windows:
        window_events = [event for event in events if parse_event_time(event['end']) > window_start and parse_event_time(event['start']) < window_end]
        free_slots = find_free_slots(window_events, window_start, window_end, timedelta(minutes=duration_minutes))
        if free_slots:
            return free_slots[0]
    return None


def main():
    # Authenticate and build service
    creds = authenticate()
    service = build('calendar', 'v3', credentials=creds)

    # Define event details (you can make these inputs)
    summary = 'New Event'
    description = 'Assignment description'
    duration_minutes = 60  # Time it takes to do the task

    # Check if after 10pm
    now = datetime.datetime.now(datetime.timezone.utc).astimezone()
    today_10pm = now.replace(hour=22, minute=0, second=0, microsecond=0)
    if now >= today_10pm:
        print("It is after 10pm, cannot create event.")
        return

    # Find free slots for the week
    window_start = max(next_10am_local(), now)
    week_windows = day_window_boundaries(window_start, DAYS_AHEAD)
    if not week_windows:
        print("There are no valid scheduling windows for the next week.")
        return

    window_end = week_windows[-1][1]
    events = fetch_events(service, window_start, window_end)
    first_slot = find_first_available_week_slot(events, window_start, duration_minutes)

    if not first_slot:
        print("No free slots available for the event in the next week.")
        return

    start_time, end_time = first_slot
    actual_end_time = start_time + timedelta(minutes=duration_minutes)
    if actual_end_time > end_time:
        print("First slot is too short.")
        return

    # Create the event
    created_event = create_event(service, summary, description, start_time, actual_end_time)
    print(f"Created event: {created_event['id']}")

    # Example: Update the event
    updated_event = created_event
    updated_event['description'] = 'An updated meeting to discuss Python projects.'
    updated_event = service.events().update(calendarId=CALENDAR_ID, eventId=created_event['id'], body=updated_event).execute()
    print(f"Updated event: {updated_event['id']}")

    # Example: Delete the event
    service.events().delete(calendarId=CALENDAR_ID, eventId=updated_event['id']).execute()
    print(f"Deleted event: {updated_event['id']}")

