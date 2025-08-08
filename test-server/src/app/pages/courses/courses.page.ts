import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { IonicModule, IonInfiniteScroll, IonRefresher, AlertController, ModalController, ToastController } from '@ionic/angular';
import { CourseService } from '../../services/course.service';
import { SearchBarComponent } from '../../components/search-bar/search-bar.component';
import { CourseDto, SimpleStringFilterDto } from '../../models/dto.types';
import { environment } from '../../../environments/environment';
import { CourseFormComponent } from '../../components/course-form/course-form.component';

@Component({
  selector: 'app-courses',
  templateUrl: './courses.page.html',
  styleUrls: ['./courses.page.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, IonicModule, SearchBarComponent]
})
export class CoursesPage implements OnInit {
  @ViewChild(IonInfiniteScroll) infiniteScroll!: IonInfiniteScroll;
  @ViewChild(IonRefresher) refresher!: IonRefresher;

  courses: CourseDto[] = [];
  currentPage = 0;
  hasNext = false;
  isLoading = false;
  searchTerm = '';

  constructor(
    private courseService: CourseService,
    private alertController: AlertController,
    private modalController: ModalController,
    private toastController: ToastController
  ) {}

  ngOnInit() {
    this.loadCourses();
  }

  async loadCourses(reset = false) {
    if (this.isLoading) return;

    this.isLoading = true;
    
    if (reset) {
      this.currentPage = 0;
      this.courses = [];
    }

    try {
      const filter: SimpleStringFilterDto = {
        filter: this.searchTerm,
        pagination: {
          pageNumber: this.currentPage,
          pageSize: environment.defaultPageSize
        }
      };

      const response = await this.courseService.filterCourses(filter).toPromise();
      
      if (response?.slice) {
        if (reset) {
          this.courses = response.slice.content;
        } else {
          this.courses.push(...response.slice.content);
        }
        this.hasNext = response.slice.hasNext;
        this.currentPage++;
      }
    } catch (error) {
      this.showError('Failed to load courses');
    } finally {
      this.isLoading = false;
      this.refresher?.complete();
    }
  }

  async onSearch(term: string) {
    this.searchTerm = term;
    await this.loadCourses(true);
  }

  async loadMore(event: any) {
    if (this.hasNext) {
      await this.loadCourses();
    }
    event.target.complete();
  }

  async refresh(event: any) {
    await this.loadCourses(true);
  }

  async addCourse() {
    const modal = await this.modalController.create({
      component: CourseFormComponent,
      componentProps: {
        course: null
      }
    });

    modal.present();

    const { data } = await modal.onWillDismiss();
    if (data?.saved) {
      await this.loadCourses(true);
      this.showSuccess('Course added successfully');
    }
  }

  async editCourse(course: CourseDto) {
    const modal = await this.modalController.create({
      component: CourseFormComponent,
      componentProps: {
        course: { ...course }
      }
    });

    modal.present();

    const { data } = await modal.onWillDismiss();
    if (data?.saved) {
      await this.loadCourses(true);
      this.showSuccess('Course updated successfully');
    }
  }

  async deleteCourse(course: CourseDto) {
    const alert = await this.alertController.create({
      header: 'Confirm Delete',
      message: `Are you sure you want to delete ${course.title}?`,
      buttons: [
        {
          text: 'Cancel',
          role: 'cancel'
        },
        {
          text: 'Delete',
          role: 'destructive',
          handler: async () => {
            try {
              await this.courseService.deleteCourse(course.id!).toPromise();
              await this.loadCourses(true);
              this.showSuccess('Course deleted successfully');
            } catch (error: any) {
              this.showError(error.message || 'Failed to delete course');
            }
          }
        }
      ]
    });

    await alert.present();
  }

  private async showError(message: string) {
    const toast = await this.toastController.create({
      message,
      duration: 3000,
      color: 'danger',
      position: 'top'
    });
    toast.present();
  }

  private async showSuccess(message: string) {
    const toast = await this.toastController.create({
      message,
      duration: 2000,
      color: 'success',
      position: 'top'
    });
    toast.present();
  }
} 