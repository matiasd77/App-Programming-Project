// Main App Component for Polis University Frontend Application - Root component that bootstraps the app

import { Component } from '@angular/core'; // Import Angular core Component decorator
import { IonApp, IonRouterOutlet } from '@ionic/angular/standalone'; // Import Ionic standalone components

// Root component that serves as the main entry point for the Angular application
@Component({
  selector: 'app-root', // CSS selector to use this component in templates
  templateUrl: 'app.component.html', // Path to the component's HTML template
  imports: [IonApp, IonRouterOutlet], // Standalone components to import
})
export class AppComponent {
  // Constructor for the app component
  constructor() {} // No dependencies to inject
}
