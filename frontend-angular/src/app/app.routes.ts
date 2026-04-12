import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { RegisterComponent } from './register/register.component';
import {CoursesComponent} from './canvasCoursePage/courses.component';
import {CourseDetailsComponent} from './courseDetailsPage/course-details.component';
import {AssignmentDetailsComponent} from './assignmentDetailsPage/assignment-details.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'courses', component: CoursesComponent },
  { path: 'courses/:id', component: CourseDetailsComponent },
  { path: 'courses/:id/assignments/:assignmentId', component: AssignmentDetailsComponent },
  { path: '**', redirectTo: '' },
];
