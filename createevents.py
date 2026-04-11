# File for the creation of events and adding them to the users calendar
from googleapiclient.discovery import build
from google_auth_oauthlib.flow import InstalledAppFlow
from google.auth.transport.requests import Request
from datetime import datetime, timedelta
import os.path
import pickle

from findfreeslots import find_free_slots, authenticate, next_10am_local, fetch_events, next_10pm_local

# Constants
SCOPES = ['https://www.googleapis.com/auth/calendar']
TOKEN_PATH = 'token.pickle'
CREDENTIALS_FILE = 'credentials.json'
CALENDAR_ID = 'hackathon'  # Replace with actual calendar ID if needed
SEARCH_WINDOW_HOURS = 12

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

    # Find free slots
    window_start = max(next_10am_local(), now)
    window_end = today_10pm
    events = fetch_events(service, window_start, window_end)
    free_slots = find_free_slots(events, window_start, window_end, timedelta(minutes=duration_minutes))

    if not free_slots:
        print("No free slots available for the event.")
        return

    # Use the first available slot
    start_time, end_time = free_slots[0]
    # Adjust end_time to fit the duration
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

