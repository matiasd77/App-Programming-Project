import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { IonHeader, IonToolbar, IonTitle, IonButtons, IonButton, IonContent, IonList, IonItem, IonLabel, IonInput, IonSpinner } from '@ionic/angular/standalone';
import { StudentService } from '../../services/student.service';
import { StudentDto, RespSingleDto } from '../../models/dto.types';

@Component({
  selector: 'app-student-form',
  templateUrl: './student-form.component.html',
  styleUrls: ['./student-form.component.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, IonHeader, IonToolbar, IonTitle, IonButtons, IonButton, IonContent, IonList, IonItem, IonLabel, IonInput, IonSpinner]
})
export class StudentFormComponent implements OnInit {
  @Input() student: StudentDto | null = null;
  @Output() saved = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  studentForm: FormGroup;
  isSubmitting = false;

  constructor(
    private formBuilder: FormBuilder,
    private studentService: StudentService
  ) {
    this.studentForm = this.formBuilder.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['']
    });
  }

  ngOnInit() {
    if (this.student) {
      this.studentForm.patchValue({
        firstName: this.student.firstName,
        lastName: this.student.lastName,
        email: this.student.email,
        phone: this.student.phone || ''
      });
    }
  }

  onSubmit() {
    if (this.studentForm.valid && !this.isSubmitting) {
      this.isSubmitting = true;

      const formData = this.studentForm.value;
      const studentData: StudentDto = {
        id: this.student?.id,
        firstName: formData.firstName,
        lastName: formData.lastName,
        email: formData.email,
        phone: formData.phone || undefined
      };

      this.studentService.upsertStudent(studentData).subscribe({
        next: (response: RespSingleDto<StudentDto>) => {
          this.isSubmitting = false;
          
          // Check if the response has errors
          if (response.error) {
            console.error('Server error:', response.error);
            this.showToast('Error: ' + response.error.message);
            return;
          }
          
          // Check if the response status indicates success
          if (response.status && Array.isArray(response.status) && response.status.length > 0) {
            // If status is an array with errors, handle them
            console.error('Server error:', response.status);
            this.showToast('Error: Server returned errors');
            return;
          }
          
          // Success - emit saved event
          this.showToast('Student saved successfully!');
          this.saved.emit();
        },
        error: (error) => {
          this.isSubmitting = false;
          console.error('Error saving student:', error);
          this.showToast('Error: ' + (error.message || 'Failed to save student'));
        }
      });
    }
  }

  onCancel() {
    this.cancelled.emit();
  }

  get isEditMode(): boolean {
    return !!this.student?.id;
  }

  private async showToast(message: string) {
    // For now, just log the message since ToastController is not available in standalone
    console.log('Toast:', message);
  }
}
