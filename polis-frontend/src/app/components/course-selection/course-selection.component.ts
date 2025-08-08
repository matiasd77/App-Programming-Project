import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonHeader, IonToolbar, IonTitle, IonButtons, IonButton, IonContent, IonList, IonItem, IonLabel, IonSearchbar, IonSpinner, IonFooter, IonRadio } from '@ionic/angular/standalone';
import { CourseService } from '../../services/course.service';
import { CourseDto, SimpleStringFilterDto, Pagination } from '../../models/dto.types';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-course-selection',
  templateUrl: './course-selection.component.html',
  styleUrls: ['./course-selection.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, IonHeader, IonToolbar, IonTitle, IonButtons, IonButton, IonContent, IonList, IonItem, IonLabel, IonSearchbar, IonSpinner, IonFooter, IonRadio]
})
export class CourseSelectionComponent implements OnInit {
  @Input() selectedCourseIds: number[] = [];
  @Input() studentId?: number;
  @Output() coursesSelected = new EventEmitter<number[]>();
  @Output() cancelled = new EventEmitter<void>();

  courses: CourseDto[] = [];
  filteredCourses: CourseDto[] = [];
  searchTerm = '';
  isLoading = false;
  selectedCourseId?: number;

  constructor(private courseService: CourseService) {}

  ngOnInit() {
    console.log('CourseSelectionComponent initialized');
    console.log('Initial selectedCourseIds:', this.selectedCourseIds);
    // For single selection, take the first selected course
    this.selectedCourseId = this.selectedCourseIds.length > 0 ? this.selectedCourseIds[0] : undefined;
    console.log('Initial selectedCourseId:', this.selectedCourseId);
    this.loadCourses();
  }

  loadCourses() {
    this.isLoading = true;
    
    const pagination: Pagination = {
      pageNumber: 0,
      pageSize: 100 // Load all courses for selection
    };

    const filter: SimpleStringFilterDto = {
      filter: this.searchTerm,
      pagination: pagination
    };

    this.courseService.filterCourses(filter).subscribe({
      next: (response) => {
        if (response.slice && response.slice.content) {
          this.courses = response.slice.content;
          this.filteredCourses = this.courses;
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading courses:', error);
        this.isLoading = false;
      }
    });
  }

  onSearchChange(event: any) {
    this.searchTerm = event.detail.value || '';
    this.filterCourses();
  }

  filterCourses() {
    if (!this.searchTerm) {
      this.filteredCourses = this.courses;
    } else {
      this.filteredCourses = this.courses.filter(course =>
        course.title.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        course.code.toLowerCase().includes(this.searchTerm.toLowerCase())
      );
    }
  }

  toggleCourse(courseId: number) {
    console.log('Toggling course:', courseId);
    // Single selection - select this course
    this.selectedCourseId = courseId;
    console.log('Selected course:', courseId);
    console.log('Current selected course:', this.selectedCourseId);
  }

  isCourseSelected(courseId: number): boolean {
    return this.selectedCourseId === courseId;
  }

  onConfirm() {
    console.log('Confirm button clicked');
    console.log('Selected course:', this.selectedCourseId);
    console.log('Selected count:', this.selectedCount);
    const selectedCourses = this.selectedCourseId ? [this.selectedCourseId] : [];
    this.coursesSelected.emit(selectedCourses);
  }

  onCancel() {
    this.cancelled.emit();
  }

  get selectedCount(): number {
    const count = this.selectedCourseId ? 1 : 0;
    console.log('Selected count:', count);
    return count;
  }
}
