import os

import requests as http_client
from fastapi import APIRouter, HTTPException
from google.oauth2.credentials import Credentials
from pydantic import BaseModel

router = APIRouter(prefix="/api/auth", tags=["auth"])

# In-memory credential store keyed by a session key.
# Keyed by "default" for the single-user dev setup.
_calendar_credentials: dict = {}


class GoogleTokenRequest(BaseModel):
    idToken: str


class CalendarCodeRequest(BaseModel):
    code: str


@router.post("/google")
async def google_auth(request: GoogleTokenRequest):
    # Accept the ID token. In production you would verify it with Google's
    # token-info endpoint or the google-auth library.
    return {"status": "ok"}


@router.post("/google/calendar")
async def google_calendar_auth(request: CalendarCodeRequest):
    client_id = os.getenv("GOOGLE_CLIENT_ID")
    client_secret = os.getenv("GOOGLE_CLIENT_SECRET")
    if not client_id or not client_secret:
        raise HTTPException(
            status_code=500,
            detail="GOOGLE_CLIENT_ID and GOOGLE_CLIENT_SECRET must be set in the environment.",
        )

    try:
        resp = http_client.post(
            "https://oauth2.googleapis.com/token",
            data={
                "code": request.code,
                "client_id": client_id,
                "client_secret": client_secret,
                "redirect_uri": "postmessage",
                "grant_type": "authorization_code",
            },
            timeout=10,
        )
        token_data = resp.json()
    except Exception as exc:
        raise HTTPException(status_code=500, detail=f"Token request failed: {exc}") from exc

    if "error" in token_data:
        raise HTTPException(status_code=400, detail=f"Token exchange failed: {token_data}")

    _calendar_credentials["default"] = Credentials(
        token=token_data["access_token"],
        refresh_token=token_data.get("refresh_token"),
        token_uri="https://oauth2.googleapis.com/token",
        client_id=client_id,
        client_secret=client_secret,
        scopes=token_data.get("scope", "").split(),
    )
    return {"status": "connected"}


def get_calendar_credentials():
    """Return stored credentials or raise 401."""
    creds = _calendar_credentials.get("default")
    if not creds:
        raise HTTPException(status_code=401, detail="Google Calendar not connected. Please connect via the register page.")
    return creds