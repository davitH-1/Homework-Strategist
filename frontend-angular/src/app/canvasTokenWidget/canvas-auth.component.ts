import {Component, inject} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {HttpClient} from '@angular/common/http';

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
export class CanvasAuthComponent {
  private http = inject(HttpClient); // Inject the HTTP service
  accessToken: string = '';
  uniqueId: string = Math.random().toString(36).substring(7);
  profile: CanvasProfile | null = null;
  isLoading: boolean = false;
  showProfileWidget: boolean = false;

  // --- New State ---
  // Tracks if the user has confirmed their identity
  isLinked: boolean = false;

  onSubmitToken() {
    if (this.isLinked) { this.onUnlink(); return; }
    if (!this.accessToken || this.accessToken.trim().length === 0) return;

    this.isLoading = true;
    this.showProfileWidget = false;

    const baseUrl = `http://localhost:8080/api/canvas`;

    // STEP 1: Sync Token
    this.http.post(`${baseUrl}/token`, this.accessToken, { responseType: 'text' })
      .subscribe({
        next: () => {
          console.log('Java synced. Now fetching profile...');

          // STEP 2: Get Profile (Generic call)
          this.http.get<CanvasProfile>(`${baseUrl}/profile`).subscribe({
            next: (profileData) => {
              this.profile = profileData;
              this.isLoading = false;
              this.showProfileWidget = true;
            },
            error: (err) => {
              this.isLoading = false;
              console.error('Profile fetch error:', err);
              alert('Token set, but Canvas rejected the profile request. Check your token permissions.');
            }
          });
        },
        error: (err) => {
          this.isLoading = false;
          console.error('Sync error:', err);
          // This is where your popup was coming from
          alert('Backend unreachable. Check if IntelliJ is running and CORS is allowed.');
        }
      });
  }

  onConfirmIdentity() {
    this.isLinked = true;
    this.showProfileWidget = false; // Hide preview as requested
    console.log('Account linked for:', this.profile?.name);
  }

  onUnlink() {
    this.isLinked = false;
    this.accessToken = '';
    this.profile = null;
    this.showProfileWidget = false;
  }

  onReset() {
    this.accessToken = '';
    this.profile = null;
    this.showProfileWidget = false;
  }
}
