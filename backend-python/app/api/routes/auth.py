import os

from fastapi import APIRouter, HTTPException
from google_auth_oauthlib.flow import Flow
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

    flow = Flow.from_client_config(
        {
            "web": {
                "client_id": client_id,
                "client_secret": client_secret,
                "auth_uri": "https://accounts.google.com/o/oauth2/auth",
                "token_uri": "https://oauth2.googleapis.com/token",
                "redirect_uris": ["postmessage"],
            }
        },
        scopes=["https://www.googleapis.com/auth/calendar"],
        redirect_uri="postmessage",
    )

    try:
        flow.fetch_token(code=request.code)
    except Exception as exc:
        raise HTTPException(status_code=400, detail=f"Token exchange failed: {exc}") from exc

    _calendar_credentials["default"] = flow.credentials
    return {"status": "connected"}


def get_calendar_credentials():
    """Return stored credentials or raise 401."""
    creds = _calendar_credentials.get("default")
    if not creds:
        raise HTTPException(status_code=401, detail="Google Calendar not connected. Please connect via the register page.")
    return creds