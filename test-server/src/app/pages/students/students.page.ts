import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { IonicModule, IonInfiniteScroll, IonRefresher, AlertController, ModalController, ToastController } from '@ionic/angular';
import { StudentService } from '../../services/student.service';
import { SearchBarComponent } from '../../components/search-bar/search-bar.component';
import { StudentDto, SimpleStringFilterDto, Pagination } from '../../models/dto.types';
import { environment } from '../../../environments/environment';
import { StudentFormComponent } from '../../components/student-form/student-form.component';

@Component({
  selector: 'app-students',
  templateUrl: './students.page.html',
  styleUrls: ['./students.page.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, IonicModule, SearchBarComponent]
})
export class StudentsPage implements OnInit {
  @ViewChild(IonInfiniteScroll) infiniteScroll!: IonInfiniteScroll;
  @ViewChild(IonRefresher) refresher!: IonRefresher;

  students: StudentDto[] = [];
  currentPage = 0;
  hasNext = false;
  isLoading = false;
  searchTerm = '';

  constructor(
    private studentService: StudentService,
    private alertController: AlertController,
    private modalController: ModalController,
    private toastController: ToastController
  ) {}

  ngOnInit() {
    this.loadStudents();
  }

  async loadStudents(reset = false) {
    if (this.isLoading) return;

    this.isLoading = true;
    
    if (reset) {
      this.currentPage = 0;
      this.students = [];
    }

    try {
      const filter: SimpleStringFilterDto = {
        filter: this.searchTerm,
        pagination: {
          pageNumber: this.currentPage,
          pageSize: environment.defaultPageSize
        }
      };

      const response = await this.studentService.filterStudents(filter).toPromise();
      
      if (response?.slice) {
        if (reset) {
          this.students = response.slice.content;
        } else {
          this.students.push(...response.slice.content);
        }
        this.hasNext = response.slice.hasNext;
        this.currentPage++;
      }
    } catch (error) {
      this.showError('Failed to load students');
    } finally {
      this.isLoading = false;
      this.refresher?.complete();
    }
  }

  async onSearch(term: string) {
    this.searchTerm = term;
    await this.loadStudents(true);
  }

  async loadMore(event: any) {
    if (this.hasNext) {
      await this.loadStudents();
    }
    event.target.complete();
  }

  async refresh(event: any) {
    await this.loadStudents(true);
  }

  async addStudent() {
    const modal = await this.modalController.create({
      component: StudentFormComponent,
      componentProps: {
        student: null
      }
    });

    modal.present();

    const { data } = await modal.onWillDismiss();
    if (data?.saved) {
      await this.loadStudents(true);
      this.showSuccess('Student added successfully');
    }
  }

  async editStudent(student: StudentDto) {
    const modal = await this.modalController.create({
      component: StudentFormComponent,
      componentProps: {
        student: { ...student }
      }
    });

    modal.present();

    const { data } = await modal.onWillDismiss();
    if (data?.saved) {
      await this.loadStudents(true);
      this.showSuccess('Student updated successfully');
    }
  }

  async deleteStudent(student: StudentDto) {
    const alert = await this.alertController.create({
      header: 'Confirm Delete',
      message: `Are you sure you want to delete ${student.firstName} ${student.lastName}?`,
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
              await this.studentService.deleteStudent(student.id!).toPromise();
              await this.loadStudents(true);
              this.showSuccess('Student deleted successfully');
            } catch (error: any) {
              this.showError(error.message || 'Failed to delete student');
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