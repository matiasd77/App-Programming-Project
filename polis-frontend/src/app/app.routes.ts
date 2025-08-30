// Application Routes Configuration for Polis University Frontend - Defines navigation structure

import { Routes } from '@angular/router'; // Import Angular router configuration type

// Define the main application routes and navigation structure
export const routes: Routes = [
  {
    path: '', // Root path
    redirectTo: 'tabs/home', // Redirect to home tab by default
    pathMatch: 'full', // Match exact path
  },
  {
    path: 'tabs', // Main tabs route
    loadComponent: () => import('./tabs/tabs.component').then((m) => m.TabsComponent), // Lazy load tabs component
    children: [ // Child routes within tabs
      {
        path: 'home', // Home tab route
        loadComponent: () => import('./pages/home/home.page').then( m => m.HomePage) // Lazy load home page
      },
      {
        path: 'students', // Students tab route
        loadComponent: () => import('./pages/students/students.page').then( m => m.StudentsPage) // Lazy load students page
      },
      {
        path: 'teachers', // Teachers tab route
        loadComponent: () => import('./pages/teachers/teachers.page').then( m => m.TeachersPage) // Lazy load teachers page
      },
      {
        path: 'courses', // Courses tab route
        loadComponent: () => import('./pages/courses/courses.page').then( m => m.CoursesPage) // Lazy load courses page
      },
      {
        path: '', // Default path within tabs
        redirectTo: 'home', // Redirect to home tab
        pathMatch: 'full', // Match exact path
      },
    ],
  },
];
