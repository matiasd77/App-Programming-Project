import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/students',
    pathMatch: 'full',
  },
  {
    path: 'students',
    loadComponent: () => import('./pages/students/students.page').then(m => m.StudentsPage)
  },
  {
    path: 'teachers',
    loadComponent: () => import('./pages/teachers/teachers.page').then(m => m.TeachersPage)
  },
  {
    path: 'courses',
    loadComponent: () => import('./pages/courses/courses.page').then(m => m.CoursesPage)
  }
]; 