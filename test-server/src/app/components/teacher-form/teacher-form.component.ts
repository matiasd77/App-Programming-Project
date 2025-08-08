import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { IonicModule, ModalController, ToastController } from '@ionic/angular';
import { TeacherService } from '../../services/teacher.service';
import { TeacherDto } from '../../models/dto.types';

@Component({
  selector: 'app-teacher-form',
  templateUrl: './teacher-form.component.html',
  styleUrls: ['./teacher-form.component.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, IonicModule]
})
export class TeacherFormComponent implements OnInit {
  @Input() teacher: TeacherDto | null = null;
  
  teacherForm: FormGroup;
  isSubmitting = false;

  constructor(
    private formBuilder: FormBuilder,
    private teacherService: TeacherService,
    private modalController: ModalController,
    private toastController: ToastController
  ) {
    this.teacherForm = this.formBuilder.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      title: ['', [Validators.required, Validators.minLength(2)]]
    });
  }

  ngOnInit() {
    if (this.teacher) {
      this.teacherForm.patchValue({
        firstName: this.teacher.firstName,
        lastName: this.teacher.lastName,
        title: this.teacher.title
      });
    }
  }

  async onSubmit() {
    if (this.teacherForm.invalid || this.isSubmitting) {
      return;
    }

    this.isSubmitting = true;

    try {
      const formData = this.teacherForm.value;
      const teacherData: TeacherDto = {
        ...formData,
        id: this.teacher?.id
      };

      await this.teacherService.upsertTeacher(teacherData).toPromise();
      
      this.modalController.dismiss({ saved: true });
    } catch (error: any) {
      this.showError(error.message || 'Failed to save teacher');
    } finally {
      this.isSubmitting = false;
    }
  }

  cancel() {
    this.modalController.dismiss();
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

  get isEditMode(): boolean {
    return !!this.teacher?.id;
  }

  get title(): string {
    return this.isEditMode ? 'Edit Teacher' : 'Add Teacher';
  }
} 