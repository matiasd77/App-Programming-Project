import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { IonicModule, IonInfiniteScroll, IonRefresher, AlertController, ModalController, ToastController } from '@ionic/angular';
import { TeacherService } from '../../services/teacher.service';
import { SearchBarComponent } from '../../components/search-bar/search-bar.component';
import { TeacherDto, SimpleStringFilterDto } from '../../models/dto.types';
import { environment } from '../../../environments/environment';
import { TeacherFormComponent } from '../../components/teacher-form/teacher-form.component';

@Component({
  selector: 'app-teachers',
  templateUrl: './teachers.page.html',
  styleUrls: ['./teachers.page.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, IonicModule, SearchBarComponent]
})
export class TeachersPage implements OnInit {
  @ViewChild(IonInfiniteScroll) infiniteScroll!: IonInfiniteScroll;
  @ViewChild(IonRefresher) refresher!: IonRefresher;

  teachers: TeacherDto[] = [];
  currentPage = 0;
  hasNext = false;
  isLoading = false;
  searchTerm = '';

  constructor(
    private teacherService: TeacherService,
    private alertController: AlertController,
    private modalController: ModalController,
    private toastController: ToastController
  ) {}

  ngOnInit() {
    this.loadTeachers();
  }

  async loadTeachers(reset = false) {
    if (this.isLoading) return;

    this.isLoading = true;
    
    if (reset) {
      this.currentPage = 0;
      this.teachers = [];
    }

    try {
      const filter: SimpleStringFilterDto = {
        filter: this.searchTerm,
        pagination: {
          pageNumber: this.currentPage,
          pageSize: environment.defaultPageSize
        }
      };

      const response = await this.teacherService.filterTeachers(filter).toPromise();
      
      if (response?.slice) {
        if (reset) {
          this.teachers = response.slice.content;
        } else {
          this.teachers.push(...response.slice.content);
        }
        this.hasNext = response.slice.hasNext;
        this.currentPage++;
      }
    } catch (error) {
      this.showError('Failed to load teachers');
    } finally {
      this.isLoading = false;
      this.refresher?.complete();
    }
  }

  async onSearch(term: string) {
    this.searchTerm = term;
    await this.loadTeachers(true);
  }

  async loadMore(event: any) {
    if (this.hasNext) {
      await this.loadTeachers();
    }
    event.target.complete();
  }

  async refresh(event: any) {
    await this.loadTeachers(true);
  }

  async addTeacher() {
    const modal = await this.modalController.create({
      component: TeacherFormComponent,
      componentProps: {
        teacher: null
      }
    });

    modal.present();

    const { data } = await modal.onWillDismiss();
    if (data?.saved) {
      await this.loadTeachers(true);
      this.showSuccess('Teacher added successfully');
    }
  }

  async editTeacher(teacher: TeacherDto) {
    const modal = await this.modalController.create({
      component: TeacherFormComponent,
      componentProps: {
        teacher: { ...teacher }
      }
    });

    modal.present();

    const { data } = await modal.onWillDismiss();
    if (data?.saved) {
      await this.loadTeachers(true);
      this.showSuccess('Teacher updated successfully');
    }
  }

  async deleteTeacher(teacher: TeacherDto) {
    const alert = await this.alertController.create({
      header: 'Confirm Delete',
      message: `Are you sure you want to delete ${teacher.firstName} ${teacher.lastName}?`,
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
              await this.teacherService.deleteTeacher(teacher.id!).toPromise();
              await this.loadTeachers(true);
              this.showSuccess('Teacher deleted successfully');
            } catch (error: any) {
              this.showError(error.message || 'Failed to delete teacher');
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