import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { IonicModule, ModalController, ToastController } from '@ionic/angular';
import { StudentService } from '../../services/student.service';
import { StudentDto } from '../../models/dto.types';

@Component({
  selector: 'app-student-form',
  templateUrl: './student-form.component.html',
  styleUrls: ['./student-form.component.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, IonicModule]
})
export class StudentFormComponent implements OnInit {
  @Input() student: StudentDto | null = null;
  
  studentForm: FormGroup;
  isSubmitting = false;

  constructor(
    private formBuilder: FormBuilder,
    private studentService: StudentService,
    private modalController: ModalController,
    private toastController: ToastController
  ) {
    this.studentForm = this.formBuilder.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      serialNumber: ['', [Validators.required, Validators.minLength(3)]]
    });
  }

  ngOnInit() {
    if (this.student) {
      this.studentForm.patchValue({
        firstName: this.student.firstName,
        lastName: this.student.lastName,
        serialNumber: this.student.serialNumber
      });
    }
  }

  async onSubmit() {
    if (this.studentForm.invalid || this.isSubmitting) {
      return;
    }

    this.isSubmitting = true;

    try {
      const formData = this.studentForm.value;
      const studentData: StudentDto = {
        ...formData,
        id: this.student?.id
      };

      await this.studentService.upsertStudent(studentData).toPromise();
      
      this.modalController.dismiss({ saved: true });
    } catch (error: any) {
      this.showError(error.message || 'Failed to save student');
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
    return !!this.student?.id;
  }

  get title(): string {
    return this.isEditMode ? 'Edit Student' : 'Add Student';
  }
} 