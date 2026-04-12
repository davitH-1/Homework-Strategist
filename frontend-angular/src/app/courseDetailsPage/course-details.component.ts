import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { CourseService, Assignment } from '../services/course.service';

@Component({
  selector: 'app-course-details',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './course-details.component.html',
  styleUrls: ['./course-details.component.scss']
})
export class CourseDetailsComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private courseService = inject(CourseService);
  private location = inject(Location);

  assignments: Assignment[] = [];
  isLoading = true;

  ngOnInit() {
    const courseId = this.route.snapshot.paramMap.get('id');
    if (courseId) {
      this.courseService.getAssignments(courseId).subscribe({
        next: (data) => {
          // Sort assignments: Closest due date at the top
          // Assignments without due dates are pushed to the bottom
          this.assignments = data.sort((a, b) => {
            if (!a.due_at) return 1;
            if (!b.due_at) return -1;
            return new Date(a.due_at).getTime() - new Date(b.due_at).getTime();
          });
          this.isLoading = false;
        },
        error: (err) => {
          console.error(err);
          this.isLoading = false;
        }
      });
    }
  }

  isOverdue(dueAt: string | null): boolean {
    if (!dueAt) {
      return false;
    }
    const now = new Date();
    const dueDate = new Date(dueAt);
    return dueDate < now;
  }

  goBack() {
    this.location.back(); // Returns to the previous page (Dashboard)
  }
}
