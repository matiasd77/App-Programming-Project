// Home Page Component for Polis University Frontend - Landing page with navigation to main sections

import { Component } from '@angular/core'; // Import Angular core Component decorator
import { CommonModule } from '@angular/common'; // Import common Angular directives like *ngIf, *ngFor
import { Router } from '@angular/router'; // Import Angular router for navigation
import { IonHeader, IonToolbar, IonTitle, IonContent, IonCard, IonCardHeader, IonCardTitle, IonCardSubtitle, IonCardContent, IonButton, IonButtons, IonIcon, IonGrid, IonRow, IonCol, IonAvatar } from '@ionic/angular/standalone'; // Import Ionic standalone components
import { addIcons } from 'ionicons'; // Import function to register custom icons
import { people, person, library, school, statsChart, settings, informationCircle, arrowForward, home, search, sync, shieldCheckmark, phonePortrait } from 'ionicons/icons'; // Import specific icons

// Home page component that serves as the landing page for the application
@Component({
  selector: 'app-home', // CSS selector to use this component in templates
  templateUrl: './home.page.html', // Path to the component's HTML template
  styleUrls: ['./home.page.scss'], // Path to the component's stylesheet
  standalone: true, // Mark as standalone component (no NgModule needed)
  imports: [CommonModule, IonHeader, IonToolbar, IonTitle, IonContent, IonCard, IonCardHeader, IonCardTitle, IonCardSubtitle, IonCardContent, IonButton, IonButtons, IonIcon, IonGrid, IonRow, IonCol, IonAvatar] // Import required modules and components
})
export class HomePage {
  // Constructor for the home page component
  constructor(private router: Router) { // Inject Angular router service
    addIcons({home,people,person,library,school,arrowForward,search,sync,shieldCheckmark,phonePortrait}); // Register custom icons for use in templates
  }

  // Method to navigate to different tabs/pages
  navigateTo(page: string) {
    this.router.navigate(['/tabs', page]); // Navigate to specified tab using router
  }
}
