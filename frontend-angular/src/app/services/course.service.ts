import {inject, Injectable} from '@angular/core';
import { Observable, of, delay } from 'rxjs';
import {HttpClient} from '@angular/common/http';

export interface Course {
  id: string;
  name: string;
  course_code: string;
  enrollment_term_id: number;
  // This field can now be a string URL or null for courses without a banner
  image_download_url: string | null;
  term_id: number;
}

@Injectable({
  providedIn: 'root'
})

@Injectable({ providedIn: 'root' })
export class CourseService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/canvas';

  getCourses(): Observable<Course[]> {
    return this.http.get<Course[]>(`${this.baseUrl}/courses`);
  }

  getAssignments(courseId: string): Observable<Assignment[]> {
    // Points to the new /api/canvas/courses/{id}/assignments endpoint
    return this.http.get<Assignment[]>(`${this.baseUrl}/courses/${courseId}/assignments`);
  }
}

export interface Assignment {
  id: number;
  course_id: number;
  name: string;
  due_at: string | null;
  description: string;
}


