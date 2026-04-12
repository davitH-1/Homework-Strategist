import { Component, OnInit, OnDestroy, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CourseService, Course } from '../services/course.service';
import { PlanService, PlanResponse } from '../services/plan.service';
import { GoogleAuthService } from '../services/google-auth.service';

export type StepStatus = 'idle' | 'pending' | 'ready' | 'failed';

export interface PlanStep {
  label: string;
  status: StepStatus;
}

@Component({
  selector: 'app-courses',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './courses.component.html',
  styleUrl: './courses.component.scss'
})
export class CoursesComponent implements OnInit, OnDestroy {
  private courseService = inject(CourseService);
  private planService = inject(PlanService);
  private auth = inject(GoogleAuthService);

  courses: Course[] = [];
  isLoading = true;

  planResult = signal<PlanResponse | null>(null);

  steps = signal<PlanStep[]>([
    { label: 'Fetching Canvas assignments', status: 'idle' },
    { label: 'Generating AI plan',          status: 'idle' },
    { label: 'Scheduling calendar',         status: 'idle' },
  ]);

  // Derived helpers for the template
  get planLoading(): boolean {
    return this.steps().some(s => s.status === 'pending');
  }
  get planFailed(): boolean {
    return this.steps().some(s => s.status === 'failed');
  }
  get planDone(): boolean {
    return this.steps().every(s => s.status === 'ready');
  }

  private stepTimers: ReturnType<typeof setTimeout>[] = [];

  ngOnInit() {
    this.courseService.getCourses().subscribe({
      next: (data) => { this.courses = data; this.isLoading = false; },
      error: () => { this.isLoading = false; }
    });
  }

  ngOnDestroy() {
    this.clearTimers();
  }

  generatePlan(): void {
    const user = this.auth.user();
    if (!user) {
      this.markFailed(0);
      return;
    }

    this.planResult.set(null);
    this.resetSteps();

    // Step 0 — starts immediately
    this.markPending(0);

    // Step 1 — after 2 s (Canvas fetch is fast, AI takes time)
    this.stepTimers.push(setTimeout(() => {
      this.markReady(0);
      this.markPending(1);
    }, 2000));

    // Step 2 — after 8 s (AI is running, transition to calendar step)
    this.stepTimers.push(setTimeout(() => {
      this.markReady(1);
      this.markPending(2);
    }, 8000));

    this.planService.generate(user.sub).subscribe({
      next: (result) => {
        this.clearTimers();
        // Mark all remaining pending/idle steps as ready
        this.steps.update(steps => steps.map(s =>
          s.status !== 'ready' ? { ...s, status: 'ready' } : s
        ));
        this.planResult.set(result);
      },
      error: (err) => {
        this.clearTimers();
        // Mark the first non-ready step as failed
        const idx = this.steps().findIndex(s => s.status !== 'ready');
        if (idx !== -1) this.markFailed(idx);
        console.error('Plan generation failed:', err);
      }
    });
  }

  // ── helpers ──────────────────────────────────────────────────────────────

  private resetSteps(): void {
    this.clearTimers();
    this.steps.set([
      { label: 'Fetching Canvas assignments', status: 'idle' },
      { label: 'Generating AI plan',          status: 'idle' },
      { label: 'Scheduling calendar',         status: 'idle' },
    ]);
  }

  private markPending(i: number): void {
    this.steps.update(steps => steps.map((s, idx) =>
      idx === i ? { ...s, status: 'pending' } : s
    ));
  }

  private markReady(i: number): void {
    this.steps.update(steps => steps.map((s, idx) =>
      idx === i ? { ...s, status: 'ready' } : s
    ));
  }

  private markFailed(i: number): void {
    this.steps.update(steps => steps.map((s, idx) =>
      idx === i ? { ...s, status: 'failed' } : s
    ));
  }

  private clearTimers(): void {
    this.stepTimers.forEach(t => clearTimeout(t));
    this.stepTimers = [];
  }
}