import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CourseService, Course } from '../services/course.service';
import { Router } from '@angular/router'; // Import Router for navigation

@Component({
  selector: 'app-courses',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './courses.component.html',
  styleUrl: './courses.component.scss'
})
export class CoursesComponent implements OnInit {
  private courseService = inject(CourseService);
  private router = inject(Router);

  // Define the missing variables
  courses: Course[] = [];
  isLoading: boolean = true;

  ngOnInit() {
    this.fetchCourses();
  }

  fetchCourses() {
    this.isLoading = true;
    this.courseService.getCourses().subscribe({
      next: (data) => {
        this.courses = data;
        this.isLoading = false;
        console.log('Courses loaded:', data);
      },
      error: (err) => {
        console.error('Failed to load courses', err);
        this.isLoading = false;
      }
    });
  }

  // Define the missing method
  goToCourseDetails(courseId: string) {
    console.log('Navigating to course:', courseId);
    // For now, this just logs. Later you can use:
    this.router.navigate(['/courses', courseId]);
  }
}
