import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { IonHeader, IonToolbar, IonTitle, IonButtons, IonButton, IonContent, IonList, IonItem, IonLabel, IonInput, IonTextarea, IonSpinner } from '@ionic/angular/standalone';
import { CourseService } from '../../services/course.service';
import { CourseDto, RespSingleDto } from '../../models/dto.types';

@Component({
  selector: 'app-course-form',
  templateUrl: './course-form.component.html',
  styleUrls: ['./course-form.component.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, IonHeader, IonToolbar, IonTitle, IonButtons, IonButton, IonContent, IonList, IonItem, IonLabel, IonInput, IonTextarea, IonSpinner]
})
export class CourseFormComponent implements OnInit {
  @Input() course: CourseDto | null = null;
  @Output() saved = new EventEmitter<void>();
  @Output() cancelled = new EventEmitter<void>();

  courseForm: FormGroup;
  isSubmitting = false;

  constructor(
    private formBuilder: FormBuilder,
    private courseService: CourseService
  ) {
    this.courseForm = this.formBuilder.group({
      title: ['', [Validators.required, Validators.minLength(2)]],
      code: ['', [Validators.required, Validators.minLength(2)]],
      description: [''],
      year: [new Date().getFullYear(), [Validators.required, Validators.min(2000), Validators.max(2030)]]
    });
  }

  ngOnInit() {
    if (this.course) {
      this.courseForm.patchValue({
        title: this.course.title,
        code: this.course.code,
        description: this.course.description || '',
        year: this.course.year || new Date().getFullYear()
      });
    }
  }

  onSubmit() {
    if (this.courseForm.valid && !this.isSubmitting) {
      this.isSubmitting = true;

      const formData = this.courseForm.value;
      const courseData: CourseDto = {
        id: this.course?.id,
        title: formData.title,
        code: formData.code,
        description: formData.description || undefined,
        year: formData.year
      };

      this.courseService.upsertCourse(courseData).subscribe({
        next: (response: RespSingleDto<CourseDto>) => {
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
          this.showToast('Course saved successfully!');
          this.saved.emit();
        },
        error: (error) => {
          this.isSubmitting = false;
          console.error('Error saving course:', error);
          this.showToast('Error: ' + (error.message || 'Failed to save course'));
        }
      });
    }
  }

  onCancel() {
    this.cancelled.emit();
  }

  get isEditMode(): boolean {
    return !!this.course?.id;
  }

  private async showToast(message: string) {
    // For now, just log the message since ToastController is not available in standalone
    console.log('Toast:', message);
  }
}
