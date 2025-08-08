import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { IonicModule, ModalController, ToastController } from '@ionic/angular';
import { CourseService } from '../../services/course.service';
import { CourseDto } from '../../models/dto.types';

@Component({
  selector: 'app-course-form',
  templateUrl: './course-form.component.html',
  styleUrls: ['./course-form.component.scss'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, IonicModule]
})
export class CourseFormComponent implements OnInit {
  @Input() course: CourseDto | null = null;
  
  courseForm: FormGroup;
  isSubmitting = false;

  constructor(
    private formBuilder: FormBuilder,
    private courseService: CourseService,
    private modalController: ModalController,
    private toastController: ToastController
  ) {
    this.courseForm = this.formBuilder.group({
      code: ['', [Validators.required, Validators.minLength(2)]],
      title: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]],
      year: [new Date().getFullYear(), [Validators.required, Validators.min(2000), Validators.max(2030)]]
    });
  }

  ngOnInit() {
    if (this.course) {
      this.courseForm.patchValue({
        code: this.course.code,
        title: this.course.title,
        description: this.course.description,
        year: this.course.year
      });
    }
  }

  async onSubmit() {
    if (this.courseForm.invalid || this.isSubmitting) {
      return;
    }

    this.isSubmitting = true;

    try {
      const formData = this.courseForm.value;
      const courseData: CourseDto = {
        ...formData,
        id: this.course?.id
      };

      await this.courseService.upsertCourse(courseData).toPromise();
      
      this.modalController.dismiss({ saved: true });
    } catch (error: any) {
      this.showError(error.message || 'Failed to save course');
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
    return !!this.course?.id;
  }

  get title(): string {
    return this.isEditMode ? 'Edit Course' : 'Add Course';
  }
} 