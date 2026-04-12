import { Injectable, signal } from '@angular/core';
import { environment } from '../../environments/environment';

export interface GoogleUser {
  sub: string;
  email: string;
  name: string;
  picture?: string;
  email_verified?: boolean;
}

export interface CalendarEventPayload {
  title: string;
  date: string;        // ISO date string, e.g. "2026-04-15"
  time?: string;       // "HH:mm", optional
  description?: string;
}

const STORAGE_KEY = 'hwstr_google_user';

@Injectable({ providedIn: 'root' })
export class GoogleAuthService {
  readonly user = signal<GoogleUser | null>(this.readStoredUser());
  readonly clientConfigured = signal(!!environment.googleClientId.trim());
  readonly calendarConnected = signal(false);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  private scriptPromise: Promise<void> | null = null;
  private initPromise: Promise<void> | null = null;

  /** Resolves after GIS script is loaded, initialized, and ready for renderButton */
  whenReady(): Promise<void> {
    if (!environment.googleClientId.trim()) {
      return Promise.resolve();
    }
    if (!this.initPromise) {
      this.initPromise = this.loadScript().then(() => {
        google.accounts.id.initialize({
          client_id: environment.googleClientId,
          callback: (resp) => this.onCredential(resp),
          auto_select: false,
          cancel_on_tap_outside: true,
        });
      });
    }
    return this.initPromise;
  }

  renderSignUpButton(container: HTMLElement): void {
    if (!environment.googleClientId.trim()) {
      return;
    }
    google.accounts.id.renderButton(container, {
      theme: 'outline',
      size: 'large',
      width: 320,
      text: 'signup_with',
      locale: 'en',
    });
  }

  /** Initiates Google OAuth2 popup to request Google Calendar access. */
  async requestCalendarAccess(): Promise<void> {
    await this.loadScript();
    this.error.set(null);
    const client = google.accounts.oauth2.initCodeClient({
      client_id: environment.googleClientId,
      scope: 'https://www.googleapis.com/auth/calendar',
      ux_mode: 'popup',
      callback: (resp) => void this.onCalendarCode(resp),
    });
    client.requestCode();
  }

  /** POSTs a calendar event payload to the backend. */
  async applyCalendarChanges(payload: CalendarEventPayload): Promise<void> {
    this.loading.set(true);
    this.error.set(null);
    try {
      const res = await fetch(`${environment.calendarBackendUrl}/api/calendar/events`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      });
      if (!res.ok) {
        throw new Error(`Backend error: ${res.status}`);
      }
    } catch (e) {
      this.error.set(e instanceof Error ? e.message : 'Failed to apply calendar changes');
      throw e;
    } finally {
      this.loading.set(false);
    }
  }

  signOut(): void {
    localStorage.removeItem(STORAGE_KEY);
    this.user.set(null);
    this.calendarConnected.set(false);
    this.error.set(null);
    try {
      if (typeof google !== 'undefined') {
        google.accounts.id.disableAutoSelect();
      }
    } catch {
      /* GIS not loaded */
    }
  }

  private loadScript(): Promise<void> {
    if (this.scriptPromise) {
      return this.scriptPromise;
    }
    this.scriptPromise = new Promise((resolve, reject) => {
      if (typeof google !== 'undefined' && google.accounts?.id) {
        resolve();
        return;
      }
      const existing = document.querySelector<HTMLScriptElement>(
        'script[src="https://accounts.google.com/gsi/client"]'
      );
      if (existing) {
        existing.addEventListener('load', () => resolve());
        existing.addEventListener('error', () => reject(new Error('Google Sign-In script failed')));
        return;
      }
      const s = document.createElement('script');
      s.src = 'https://accounts.google.com/gsi/client';
      s.async = true;
      s.defer = true;
      s.onload = () => resolve();
      s.onerror = () => reject(new Error('Google Sign-In script failed'));
      document.head.appendChild(s);
    });
    return this.scriptPromise;
  }

  private onCredential(resp: { credential: string }): void {
    const payload = this.decodeJwt(resp.credential);
    const user: GoogleUser = {
      sub: String(payload['sub'] ?? ''),
      email: String(payload['email'] ?? ''),
      name: String(payload['name'] ?? ''),
      picture: payload['picture'] ? String(payload['picture']) : undefined,
      email_verified: Boolean(payload['email_verified']),
    };
    localStorage.setItem(STORAGE_KEY, JSON.stringify(user));
    this.user.set(user);
    void this.sendTokenToBackend(resp.credential);
  }

  private async sendTokenToBackend(idToken: string): Promise<void> {
    this.loading.set(true);
    this.error.set(null);
    try {
      const res = await fetch(`${environment.calendarBackendUrl}/api/auth/google`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ idToken }),
      });
      if (!res.ok) {
        throw new Error(`Backend error: ${res.status}`);
      }
    } catch (e) {
      this.error.set(e instanceof Error ? e.message : 'Backend request failed');
    } finally {
      this.loading.set(false);
    }
  }

  private async onCalendarCode(resp: { code: string; error?: string }): Promise<void> {
    if (resp.error || !resp.code) {
      this.error.set(resp.error ?? 'Calendar authorization cancelled');
      return;
    }
    this.loading.set(true);
    this.error.set(null);
    try {
      const res = await fetch(`${environment.calendarBackendUrl}/api/auth/google/calendar`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ code: resp.code }),
      });
      if (!res.ok) {
        throw new Error(`Backend error: ${res.status}`);
      }
      this.calendarConnected.set(true);
    } catch (e) {
      this.error.set(e instanceof Error ? e.message : 'Calendar authorization failed');
    } finally {
      this.loading.set(false);
    }
  }

  private decodeJwt(token: string): Record<string, unknown> {
    const part = token.split('.')[1];
    let b64 = part.replace(/-/g, '+').replace(/_/g, '/');
    const pad = b64.length % 4;
    if (pad) {
      b64 += '='.repeat(4 - pad);
    }
    const json = atob(b64);
    return JSON.parse(json) as Record<string, unknown>;
  }

  private readStoredUser(): GoogleUser | null {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) {
      return null;
    }
    try {
      return JSON.parse(raw) as GoogleUser;
    } catch {
      return null;
    }
  }
}