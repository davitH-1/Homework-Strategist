import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

interface CanvasProfile {
  id: number;
  name: string;
  primary_email: string;
  avatar_url: string;
}

@Component({
  selector: 'app-canvas-auth',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './canvas-auth.component.html',
  styleUrl: './canvas-auth.component.scss'
})
export class CanvasAuthComponent implements OnInit{
  private http = inject(HttpClient);
  accessToken: string = '';
  uniqueId: string = Math.random().toString(36).substring(7);
  profile: CanvasProfile | null = null;
  isLoading: boolean = false;
  showProfileWidget: boolean = false;
  isLinked: boolean = false;

  ngOnInit() {
    this.checkPersistence();
  }

  private checkPersistence() {
    const baseUrl = `http://localhost:8080/api/canvas`;
    // We try to fetch the profile. If it works, the backend already has a valid token.
    this.http.get<CanvasProfile>(`${baseUrl}/profile`).subscribe({
      next: (profileData) => {
        this.profile = profileData;
        this.isLinked = true;
        this.showProfileWidget = false;
      },
      error: () => {
        this.isLinked = false;
        console.log('No active session found.');
      }
    });
  }

  onSubmitToken() {
    if (this.isLinked) {
      this.onUnlink();
      return;
    }

    if (!this.accessToken || this.accessToken.trim().length === 0) return;

    this.isLoading = true;
    this.showProfileWidget = false;

    const baseUrl = `http://localhost:8080/api/canvas`;

    // FAILSAFE: Clear database before setting a new token
    this.http.delete(`${baseUrl}/clear`).subscribe({
      next: () => this.proceedToSaveToken(baseUrl),
      error: (err) => {
        if (err.status === 200) {
          this.proceedToSaveToken(baseUrl);
        } else {
          this.handleError(err);
        }
      }
    });
  }

  // Wrap the next steps in a private method
  private proceedToSaveToken(baseUrl: string) {
    const body = { token: this.accessToken };
    this.http.post(`${baseUrl}/token`, body, { responseType: 'text' }).subscribe({
      next: () => this.fetchProfile(baseUrl),
      error: (err) => this.handleError(err)
    });
  }

  onConfirmIdentity() {
    if (!this.accessToken) return;

    this.isLoading = true;
    const baseUrl = `http://localhost:8080/api/canvas`;

    // Calling your @PostMapping("/sync") with ivctoken as a Param
    this.http.post(`${baseUrl}/sync?ivctoken=${this.accessToken}`, {}, { responseType: 'text' })
      .subscribe({
        next: (response) => {
          console.log('Database sync complete:', response);
          this.isLinked = true;
          this.showProfileWidget = false;
          this.isLoading = false;
        },
        error: (err) => {
          this.isLoading = false;
          console.error('Sync failed:', err);
          alert('Identity confirmed, but data sync failed.');
        }
      });
  }

  onUnlink() {
    const baseUrl = `http://localhost:8080/api/canvas`;
    this.isLoading = true;

    // Call /clear to wipe the DB and the token association in Java
    this.http.delete(`${baseUrl}/clear`).subscribe({
      next: () => this.resetLocalState(),
      error: (err) => {
        // Failsafe: if Spring returns 200 but Angular fails to parse the JSON
        if (err.status === 200) {
          this.resetLocalState();
        } else {
          this.handleError(err);
        }
      }
    });
  }

// Helper to keep code DRY
  private resetLocalState() {
    this.isLinked = false;
    this.accessToken = '';
    this.profile = null;
    this.showProfileWidget = false;
    this.isLoading = false;
  }

  onReset() {
    this.accessToken = '';
    this.profile = null;
    this.showProfileWidget = false;
  }

  private fetchProfile(baseUrl: string) {
    this.http.get<CanvasProfile>(`${baseUrl}/profile`).subscribe({
      next: (profileData) => {
        this.profile = profileData;
        this.isLoading = false;
        this.showProfileWidget = true;
      },
      // error: (err) => this.handleError(err)
    });
  }
  private handleError(err: any) {
    this.isLoading = false;
    console.error('Detailed Error:', err);

    // Don't show alert if the status is actually successful
    if (err.status >= 200 && err.status < 300) {
      return;
    }

    alert(`Error: ${err.message || 'Communication error with backend'}`);
  }

}
