import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CourseService, Course } from '../services/course.service';

@Component({
  selector: 'app-courses',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './courses.component.html',
  styleUrl: './courses.component.scss'
})
export class CoursesComponent implements OnInit {
  private courseService = inject(CourseService);
  courses: Course[] = [];
  isLoading = true;

  ngOnInit() {
    this.courseService.getCourses().subscribe({
      next: (data) => {
        this.courses = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Fetch failed', err);
        this.isLoading = false;
      }
    });
  }
}
