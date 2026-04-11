import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  imports: [RouterLink],
  template: `
    <section class="panel">
      <h1>Welcome</h1>
      <p>
        Create an account with your Google profile to get started.
      </p>
      <a routerLink="/register" class="cta">Register with Google</a>
    </section>
  `,
  styles: `
    .panel {
      max-width: 32rem;
      margin: 0 auto;
      padding: 2rem;
    }
    h1 {
      font-size: 1.75rem;
      font-weight: 600;
      margin: 0 0 0.75rem;
      letter-spacing: -0.02em;
    }
    p {
      margin: 0 0 1.5rem;
      color: var(--app-muted);
      line-height: 1.5;
    }
    .cta {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      padding: 0.65rem 1.25rem;
      border-radius: 0.5rem;
      background: var(--app-accent);
      color: #fff;
      font-weight: 500;
      text-decoration: none;
      transition: filter 0.15s ease;
    }
    .cta:hover {
      filter: brightness(1.05);
    }
  `,
})
export class HomeComponent {}
