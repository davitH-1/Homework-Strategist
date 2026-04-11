# calendar id:2acf7bf039641a738728e347242e31c6725b7a9f4717b8899ec17ff3ce3cebc8@group.calendar.google.com
import datetime
import os.path
import pickle

from google.auth.transport.requests import Request
from google.oauth2.credentials import Credentials
from google_auth_oauthlib.flow import InstalledAppFlow
from googleapiclient.discovery import build
from googleapiclient.errors import HttpError

SCOPES = ['https://www.googleapis.com/auth/calendar']
TOKEN_PATH = 'token.pickle'
CREDENTIALS_FILE = 'credentials.json'
CALENDAR_ID = 'hackathon'
SEARCH_WINDOW_HOURS = 12
MIN_SLOT_MINUTES = 30


def next_10am_local():
    now = datetime.datetime.now(datetime.timezone.utc).astimezone()
    target = now.replace(hour=10, minute=0, second=0, microsecond=0)
    if now >= target:
        target += datetime.timedelta(days=1)
    return target


def next_10pm_local():
    now = datetime.datetime.now(datetime.timezone.utc).astimezone()
    target = now.replace(hour=22, minute=0, second=0, microsecond=0)
    if now >= target:
        target += datetime.timedelta(days=1)
    return target


def authenticate():
    creds = None
    if os.path.exists(TOKEN_PATH):
        with open(TOKEN_PATH, 'rb') as token:
            creds = pickle.load(token)

    if not creds or not creds.valid:
        if creds and creds.expired and creds.refresh_token:
            creds.refresh(Request())
        else:
            flow = InstalledAppFlow.from_client_secrets_file(CREDENTIALS_FILE, SCOPES)
            creds = flow.run_local_server(port=0)
        with open(TOKEN_PATH, 'wb') as token:
            pickle.dump(creds, token)

    return creds


def parse_event_time(event_time):
    if 'dateTime' in event_time:
        return datetime.datetime.fromisoformat(event_time['dateTime'])
    return datetime.datetime.fromisoformat(event_time['date']).replace(tzinfo=datetime.timezone.utc)


def fetch_events(service, time_min, time_max):
    return service.events().list(
        calendarId=CALENDAR_ID,
        timeMin=time_min.isoformat(),
        timeMax=time_max.isoformat(),
        singleEvents=True,
        orderBy='startTime',
        maxResults=250,
    ).execute().get('items', [])


def find_free_slots(events, window_start, window_end, min_duration=datetime.timedelta(minutes=MIN_SLOT_MINUTES)):
    busy_periods = []
    for event in events:
        start = parse_event_time(event['start'])
        end = parse_event_time(event['end'])
        busy_periods.append((start, end))

    busy_periods.sort(key=lambda period: period[0])

    free_slots = []
    cursor = window_start
    for start, end in busy_periods:
        if end <= cursor:
            continue
        if start > cursor:
            gap = start - cursor
            if gap >= min_duration:
                free_slots.append((cursor, start))
            cursor = end
        else:
            cursor = max(cursor, end)

    if cursor < window_end:
        gap = window_end - cursor
        if gap >= min_duration:
            free_slots.append((cursor, window_end))

    return free_slots


def categorize_slots(slots, interval_minutes=MIN_SLOT_MINUTES):
    buckets = {}
    for start, end in slots:
        total_minutes = int((end - start).total_seconds() // 60)
        bucket_size = ((total_minutes + interval_minutes - 1) // interval_minutes) * interval_minutes
        bucket_label = f'{bucket_size} min'
        buckets.setdefault(bucket_label, []).append((start, end, total_minutes))
    return buckets


def format_slot(slot):
    start, end, minutes = slot
    return f'{start.isoformat()} → {end.isoformat()} ({minutes} min)'


def print_free_slot_buckets(buckets):
    if not buckets:
        print('No free slots of at least 30 minutes were found.')
        return

    for bucket in sorted(buckets.keys(), key=lambda label: int(label.split()[0])):
        print(f'\nFree slots in {bucket} buckets:')
        for slot in buckets[bucket]:
            print(f'  - {format_slot(slot)}')


def main():
    creds = authenticate()
    service = build('calendar', 'v3', credentials=creds)

    now = datetime.datetime.now(datetime.timezone.utc).astimezone()
    today_10pm = now.replace(hour=22, minute=0, second=0, microsecond=0)
    if now >= today_10pm:
        print("It is after 10pm, no slots available.")
        return

    window_start = max(next_10am_local(), now)
    window_end = today_10pm
    print(f'Checking free slots from {window_start.isoformat()} to {window_end.isoformat()}')

    try:
        events = fetch_events(service, window_start, window_end)
        free_slots = find_free_slots(events, window_start, window_end)
        buckets = categorize_slots(free_slots)
        print_free_slot_buckets(buckets)
    except HttpError as error:
        print(f'An error occurred: {error}')


if __name__ == '__main__':
    main()
