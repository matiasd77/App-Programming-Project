import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { IonHeader, IonToolbar, IonTitle, IonContent, IonCard, IonCardHeader, IonCardTitle, IonCardSubtitle, IonCardContent, IonButton, IonButtons, IonIcon, IonGrid, IonRow, IonCol, IonAvatar } from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import { people, person, library, school, statsChart, settings, informationCircle, arrowForward, home, search, sync, shieldCheckmark, phonePortrait } from 'ionicons/icons';

@Component({
  selector: 'app-home',
  templateUrl: './home.page.html',
  styleUrls: ['./home.page.scss'],
  standalone: true,
  imports: [CommonModule, IonHeader, IonToolbar, IonTitle, IonContent, IonCard, IonCardHeader, IonCardTitle, IonCardSubtitle, IonCardContent, IonButton, IonButtons, IonIcon, IonGrid, IonRow, IonCol, IonAvatar]
})
export class HomePage {
  constructor(private router: Router) {
    addIcons({home,people,person,library,school,arrowForward,search,sync,shieldCheckmark,phonePortrait});
  }

  navigateTo(page: string) {
    this.router.navigate(['/tabs', page]);
  }
}
