import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

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
  accessToken: string = '';
  uniqueId: string = Math.random().toString(36).substring(7);
  profile: CanvasProfile | null = null;
  isLoading: boolean = false;
  showProfileWidget: boolean = false;

  // --- New State ---
  // Tracks if the user has confirmed their identity
  isLinked: boolean = false;

  onSubmitToken() {
    // If already linked, this button now acts as the "Unlink" trigger
    if (this.isLinked) {
      this.onUnlink();
      return;
    }

    this.showProfileWidget = false;

    if (!this.accessToken || this.accessToken.trim().length === 0) {
      this.profile = null;
      return;
    }

    this.isLoading = true;

    // Mocking the backend response
    setTimeout(() => {
      this.profile = {
        id: 236306,
        name: "Erfan Tavassoli",
        primary_email: "etavassoli1@ivc.edu",
        avatar_url: "https://ivc-new.instructure.com/images/thumbnails/6008~16519330/UyedxP7Ny2rfqm6Zgzz9IDlyDCEHEk6TDvEizxQM"
      };

      this.isLoading = false;
      this.showProfileWidget = true;
    }, 1200);
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
