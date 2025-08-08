import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { IonHeader, IonToolbar, IonTitle, IonButtons, IonButton, IonContent, IonList, IonItem, IonLabel, IonInput, IonSpinner } from '@ionic/angular/standalone';
import { TeacherService } from '../../services/teacher.service';
import { TeacherDto, RespSingleDto } from '../../models/dto.types';

@Component({
  selector: 'app-teacher-form',
  templateUrl: './teacher-form.component.html',
  styleUrls: ['./teacher-form.component.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, IonHeader, IonToolbar, IonTitle, IonButtons, IonButton, IonContent, IonList, IonItem, IonLabel, IonInput, IonSpinner]
})
export class TeacherFormComponent implements OnInit {
  @Input() teacher: TeacherDto | null = null;
  @Output() saved = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  teacherForm: FormGroup;
  isSubmitting = false;

  constructor(
    private formBuilder: FormBuilder,
    private teacherService: TeacherService
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

  onSubmit() {
    if (this.teacherForm.valid && !this.isSubmitting) {
      this.isSubmitting = true;

      const formData = this.teacherForm.value;
      const teacherData: TeacherDto = {
        id: this.teacher?.id,
        firstName: formData.firstName,
        lastName: formData.lastName,
        title: formData.title
      };

      this.teacherService.upsertTeacher(teacherData).subscribe({
        next: (response: RespSingleDto<TeacherDto>) => {
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
          this.showToast('Teacher saved successfully!');
          this.saved.emit();
        },
        error: (error) => {
          this.isSubmitting = false;
          console.error('Error saving teacher:', error);
          this.showToast('Error: ' + (error.message || 'Failed to save teacher'));
        }
      });
    }
  }

  onCancel() {
    this.cancelled.emit();
  }

  get isEditMode(): boolean {
    return !!this.teacher?.id;
  }

  private async showToast(message: string) {
    // For now, just log the message since ToastController is not available in standalone
    console.log('Toast:', message);
  }
}
