import {
  AfterViewInit,
  Component,
  ElementRef,
  OnDestroy,
  ViewChild,
  inject,
  signal,
} from '@angular/core';
import { RouterLink } from '@angular/router';
import { GoogleAuthService } from '../services/google-auth.service';
import {CanvasAuthComponent} from '../canvasTokenWidget/canvas-auth.component';

@Component({
  selector: 'app-register',
  imports: [RouterLink, CanvasAuthComponent],
  template: `
    <section class="panel">
      <a routerLink="/" class="back">← Home</a>

      @if (!auth.clientConfigured()) {
        <div class="notice warn" role="status">
          <strong>Configure Google OAuth</strong>
          <p>
            Set <code>googleClientId</code> in
            <code>src/environments/environment.ts</code>
            (Web client ID from
            <a
              href="https://console.cloud.google.com/apis/credentials"
              target="_blank"
              rel="noopener noreferrer"
              >Google Cloud Console</a
            >). Add <code>http://localhost:4200</code> to authorized JavaScript origins.
          </p>
        </div>
      }

      @if (auth.user(); as u) {
        <div class="success">
          <div class="profile">
            @if (u.picture) {
              <img [src]="u.picture" [alt]="u.name" width="64" height="64" />
            }
            <div>
              <h1>Account ready</h1>
              <p class="name">{{ u.name }}</p>
              <p class="email">{{ u.email }}</p>
            </div>
          </div>

          @if (auth.error()) {
            <p class="error-msg" role="alert">{{ auth.error() }}</p>
          }

          @if (!auth.calendarConnected()) {
            <div class="calendar-connect">
              <h2>Connect Google Calendar</h2>
              <p class="hint">
                Allow this app to manage your Google Calendar so you can apply changes directly.
              </p>
              <button
                type="button"
                class="primary"
                [disabled]="auth.loading()"
                (click)="connectCalendar()"
              >
                {{ auth.loading() ? 'Connecting…' : 'Connect Google Calendar' }}
              </button>
            </div>
          } @else {
            <div class="calendar-form">
              <h2>Apply Calendar Changes</h2>

              @if (eventSuccess()) {
                <div class="notice success-notice" role="status">
                  Event added to your Google Calendar.
                  <button type="button" class="link-btn" (click)="eventSuccess.set(false)">Add another</button>
                </div>
              } @else {
                <form (submit)="onSubmitEvent($event, titleRef, dateRef, timeRef, descRef)">
                  <label>
                    Event title <span class="req">*</span>
                    <input #titleRef type="text" placeholder="e.g. Team standup" required />
                  </label>
                  <label>
                    Date <span class="req">*</span>
                    <input #dateRef type="date" required />
                  </label>
                  <label>
                    Time (optional)
                    <input #timeRef type="time" />
                  </label>
                  <label>
                    Description (optional)
                    <textarea #descRef rows="3" placeholder="Add details…"></textarea>
                  </label>
                  @if (auth.error()) {
                    <p class="error-msg" role="alert">{{ auth.error() }}</p>
                  }
                  <button type="submit" class="primary" [disabled]="auth.loading()">
                    {{ auth.loading() ? 'Saving…' : 'Add to Calendar' }}
                  </button>
                </form>
              }
            </div>
          }

          <button type="button" class="secondary" (click)="auth.signOut()">Sign out</button>
        </div>
      } @else {
        <div class="register-block">
          <h1>Create your account</h1>
          <p class="lead">Use your Google account to register—no separate password.</p>
          <div #googleButtonHost class="google-host"></div>
        </div>
      }
      <app-canvas-auth></app-canvas-auth>
    </section>
  `,
  styles: `
    .panel {
      max-width: 28rem;
      margin: 0 auto;
      padding: 2rem 1.5rem;
    }
    .back {
      display: inline-block;
      margin-bottom: 1.5rem;
      color: var(--app-muted);
      text-decoration: none;
      font-size: 0.9rem;
    }
    .back:hover { color: var(--app-text); }
    .notice {
      padding: 1rem 1.1rem;
      border-radius: 0.5rem;
      margin-bottom: 1.5rem;
      font-size: 0.9rem;
      line-height: 1.45;
    }
    .notice.warn {
      background: #fff8e6;
      border: 1px solid #f0e0b2;
      color: #5c4a00;
    }
    .notice.success-notice {
      background: #edfaf3;
      border: 1px solid #b2e4c8;
      color: #1a5c38;
      display: flex;
      align-items: center;
      gap: 0.75rem;
    }
    .notice code {
      font-size: 0.82em;
      padding: 0.1em 0.35em;
      border-radius: 0.25rem;
      background: rgba(0,0,0,0.06);
    }
    .notice a { color: inherit; font-weight: 500; }
    h1 {
      font-size: 1.5rem;
      font-weight: 600;
      margin: 0 0 0.5rem;
      letter-spacing: -0.02em;
    }
    h2 {
      font-size: 1.1rem;
      font-weight: 600;
      margin: 0 0 0.4rem;
    }
    .lead {
      margin: 0 0 1.25rem;
      color: var(--app-muted);
      line-height: 1.5;
    }
    .google-host {
      min-height: 44px;
      display: flex;
      justify-content: center;
    }
    .success .profile {
      display: flex;
      gap: 1rem;
      align-items: center;
      margin-bottom: 1.5rem;
    }
    .success img {
      border-radius: 50%;
      object-fit: cover;
    }
    .success .name { font-weight: 600; margin: 0 0 0.15rem; }
    .success .email { margin: 0; font-size: 0.9rem; color: var(--app-muted); }
    .calendar-connect, .calendar-form {
      padding: 1.25rem;
      border: 1px solid var(--app-border, #e5e7eb);
      border-radius: 0.5rem;
      margin-bottom: 1.25rem;
    }
    .calendar-connect .hint {
      font-size: 0.875rem;
      color: var(--app-muted);
      line-height: 1.45;
      margin: 0 0 1rem;
    }
    form {
      display: flex;
      flex-direction: column;
      gap: 0.85rem;
    }
    label {
      display: flex;
      flex-direction: column;
      gap: 0.3rem;
      font-size: 0.875rem;
      font-weight: 500;
    }
    label input, label textarea {
      padding: 0.45rem 0.6rem;
      border: 1px solid var(--app-border, #d1d5db);
      border-radius: 0.375rem;
      font: inherit;
      font-size: 0.9rem;
    }
    label textarea { resize: vertical; }
    .req { color: #e53e3e; }
    .error-msg {
      font-size: 0.875rem;
      color: #c53030;
      margin: 0;
      padding: 0.5rem 0.75rem;
      background: #fff5f5;
      border: 1px solid #feb2b2;
      border-radius: 0.375rem;
    }
    .primary {
      padding: 0.55rem 1.1rem;
      border-radius: 0.4rem;
      border: none;
      background: var(--app-accent, #2563eb);
      color: #fff;
      font: inherit;
      font-weight: 500;
      cursor: pointer;
      transition: filter 0.15s;
    }
    .primary:hover:not(:disabled) { filter: brightness(1.08); }
    .primary:disabled { opacity: 0.6; cursor: not-allowed; }
    .secondary {
      padding: 0.5rem 1rem;
      border-radius: 0.4rem;
      border: 1px solid var(--app-border, #d1d5db);
      background: #fff;
      cursor: pointer;
      font: inherit;
    }
    .secondary:hover { background: #f7f7f7; }
    .link-btn {
      background: none;
      border: none;
      color: inherit;
      text-decoration: underline;
      cursor: pointer;
      font: inherit;
      font-size: 0.875rem;
      padding: 0;
    }
  `,
})
export class RegisterComponent implements AfterViewInit, OnDestroy {
  protected readonly auth = inject(GoogleAuthService);
  protected readonly eventSuccess = signal(false);

  @ViewChild('googleButtonHost') private googleHost?: ElementRef<HTMLElement>;

  private buttonRendered = false;

  ngAfterViewInit(): void {
    void this.tryRenderButton();
  }

  ngOnDestroy(): void {
    this.buttonRendered = false;
  }

  protected connectCalendar(): void {
    void this.auth.requestCalendarAccess();
  }

  protected onSubmitEvent(
    event: Event,
    titleRef: HTMLInputElement,
    dateRef: HTMLInputElement,
    timeRef: HTMLInputElement,
    descRef: HTMLTextAreaElement,
  ): void {
    event.preventDefault();
    void this.auth
      .applyCalendarChanges({
        title: titleRef.value.trim(),
        date: dateRef.value,
        time: timeRef.value || undefined,
        description: descRef.value.trim() || undefined,
      })
      .then(() => {
        this.eventSuccess.set(true);
        titleRef.value = '';
        dateRef.value = '';
        timeRef.value = '';
        descRef.value = '';
      })
      .catch(() => {
        /* error already set on service */
      });
  }

  private async tryRenderButton(): Promise<void> {
    if (this.auth.user() || !this.auth.clientConfigured()) {
      return;
    }
    await this.auth.whenReady();
    const el = this.googleHost?.nativeElement;
    if (!el || this.buttonRendered || this.auth.user()) {
      return;
    }
    el.replaceChildren();
    this.auth.renderSignUpButton(el);
    this.buttonRendered = true;
  }
}
