// Tabs Component for Polis University Frontend - Manages the main tab navigation structure

import { Component } from '@angular/core'; // Import Angular core Component decorator
import { IonTabs, IonTabBar, IonTabButton, IonIcon, IonLabel } from '@ionic/angular/standalone'; // Import Ionic standalone tab components
import { addIcons } from 'ionicons'; // Import function to register custom icons
import { people, person, library, add, home } from 'ionicons/icons'; // Import specific icons for tabs

// Component that manages the main tab navigation for the application
@Component({
  selector: 'app-tabs', // CSS selector to use this component in templates
  templateUrl: 'tabs.component.html', // Path to the component's HTML template
  styleUrls: ['tabs.component.scss'], // Path to the component's stylesheet
  standalone: true, // Mark as standalone component (no NgModule needed)
  imports: [IonTabs, IonTabBar, IonTabButton, IonIcon, IonLabel], // Import required Ionic components
})
export class TabsComponent {
  // Constructor for the tabs component
  constructor() {
    addIcons({ people, person, library, add, home }); // Register custom icons for use in templates
  }
}

