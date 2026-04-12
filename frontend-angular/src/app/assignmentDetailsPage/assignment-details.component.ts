import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {ActivatedRoute, Router, RouterModule} from '@angular/router';
import { CourseService, Assignment } from '../services/course.service';

@Component({
  selector: 'app-assignment-details',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './assignment-details.component.html',
  styleUrl: './assignment-details.component.scss'
})
export class AssignmentDetailsComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router); // Inject Router
  private courseService = inject(CourseService);

  assignment: Assignment | null = null;
  isLoading = true;

  ngOnInit() {
    const cId = this.route.snapshot.paramMap.get('id');
    const aId = this.route.snapshot.paramMap.get('assignmentId');

    console.log('Route Params:', { cId, aId }); // Add this to debug in the console

    if (cId && aId) {
      this.courseService.getAssignmentDetails(cId, aId).subscribe({
        next: (data) => {
          this.assignment = data;
          this.isLoading = false;
        },
        error: (err) => {
          console.error('API Error:', err);
          this.isLoading = false;
        }
      });
    } else {
      this.isLoading = false;
      console.warn('Missing IDs in URL');
    }
  }

  goBack() {
    this.router.navigate(['../..'], { relativeTo: this.route });
  }
}
