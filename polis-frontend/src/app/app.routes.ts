import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'tabs/home',
    pathMatch: 'full',
  },
  {
    path: 'tabs',
    loadComponent: () => import('./tabs/tabs.component').then((m) => m.TabsComponent),
    children: [
      {
        path: 'home',
        loadComponent: () => import('./pages/home/home.page').then( m => m.HomePage)
      },
      {
        path: 'students',
        loadComponent: () => import('./pages/students/students.page').then( m => m.StudentsPage)
      },
      {
        path: 'teachers',
        loadComponent: () => import('./pages/teachers/teachers.page').then( m => m.TeachersPage)
      },
      {
        path: 'courses',
        loadComponent: () => import('./pages/courses/courses.page').then( m => m.CoursesPage)
      },
      {
        path: '',
        redirectTo: 'home',
        pathMatch: 'full',
      },
    ],
  },
];
