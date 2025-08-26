import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { IonHeader, IonToolbar, IonTitle, IonContent, IonRefresher, IonRefresherContent, IonList, IonItem, IonLabel, IonButton, IonIcon, IonInfiniteScroll, IonInfiniteScrollContent, IonFab, IonFabButton, IonModal, IonButtons } from '@ionic/angular/standalone';
import { FormsModule } from '@angular/forms';
import { addIcons } from 'ionicons';
import { add, trash, library, home, people, person } from 'ionicons/icons';
import { forkJoin } from 'rxjs';
import { TeacherService } from '../../services/teacher.service';
import { CourseService } from '../../services/course.service';
import { SearchBarComponent } from '../../components/search-bar/search-bar.component';
import { TeacherFormComponent } from '../../components/teacher-form/teacher-form.component';
import { CourseSelectionComponent } from '../../components/course-selection/course-selection.component';
import { TeacherDto, SimpleStringFilterDto, Pagination, LongIdDto, CourseDto } from '../../models/dto.types';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-teachers',
  templateUrl: './teachers.page.html',
  styleUrls: ['./teachers.page.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, SearchBarComponent, TeacherFormComponent, CourseSelectionComponent, IonHeader, IonToolbar, IonTitle, IonContent, IonRefresher, IonRefresherContent, IonList, IonItem, IonLabel, IonButton, IonIcon, IonInfiniteScroll, IonInfiniteScrollContent, IonFab, IonFabButton, IonModal, IonButtons]
})
export class TeachersPage implements OnInit {
  teachers: TeacherDto[] = [];
  currentPage = 0;
  hasNextPage = true;
  isLoading = false;
  searchQuery = '';
  isModalOpen = false;
  isCourseModalOpen = false;
  selectedTeacher: TeacherDto | null = null;
  selectedTeacherForCourses: TeacherDto | null = null;

  constructor(
    private teacherService: TeacherService,
    private courseService: CourseService,
    private router: Router
  ) {
    addIcons({home,people,person,library,trash,add});
  }

  navigateTo(page: string) {
    this.router.navigate(['/tabs', page]);
  }

  ngOnInit() {
    this.loadTeachers(true);
  }

  onSearch(searchTerm: string) {
    this.searchQuery = searchTerm;
    this.loadTeachers(true);
  }

  loadTeachers(reset: boolean = false) {
    if (this.isLoading || (!reset && !this.hasNextPage)) return;

    if (reset) {
      this.currentPage = 0;
      this.hasNextPage = true;
      this.teachers = [];
    }

    this.isLoading = true;

    const pagination: Pagination = {
      pageNumber: this.currentPage,
      pageSize: environment.pageSize
    };

    const filter: SimpleStringFilterDto = {
      filter: this.searchQuery,
      pagination: pagination
    };

    this.teacherService.filterTeachers(filter).subscribe({
      next: (response) => {
        if (response.slice && response.slice.content) {
          this.teachers.push(...response.slice.content);
          this.hasNextPage = response.slice.hasNext;
          this.currentPage++;
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading teachers:', error);
        this.isLoading = false;
      }
    });
  }

  loadMore(event: any) {
    this.loadTeachers(false);
    event.target.complete();
  }

  refresh(event: any) {
    this.loadTeachers(true);
    event.target.complete();
  }

  openAddModal() {
    this.selectedTeacher = null;
    this.isModalOpen = true;
  }

  openEditModal(teacher: TeacherDto) {
    this.selectedTeacher = { ...teacher };
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
    this.selectedTeacher = null;
  }

  onTeacherSaved() {
    this.closeModal();
    this.loadTeachers(true);
  }

  async deleteTeacher(teacher: TeacherDto) {
    if (!teacher.id) return;

    if (confirm(`Are you sure you want to delete ${teacher.firstName} ${teacher.lastName}?`)) {
      this.performDelete(teacher.id!);
    }
  }

  private performDelete(teacherId: number) {
    this.teacherService.deleteTeacher(teacherId).subscribe({
      next: () => {
        this.loadTeachers(true);
        this.showToast('Teacher deleted successfully');
      },
      error: (error) => {
        this.showToast(error.message);
      }
    });
  }

  // Course management methods
  openCourseModal(teacher: TeacherDto) {
    this.selectedTeacherForCourses = { ...teacher };
    this.isCourseModalOpen = true;
  }

  closeCourseModal() {
    this.isCourseModalOpen = false;
    this.selectedTeacherForCourses = null;
  }

  onCoursesSelected(courseIds: number[]) {
    if (!this.selectedTeacherForCourses?.id) return;

    // Get current course associations
    const currentCourseIds = this.selectedTeacherForCourses.courses?.map(c => c.id!).filter(id => id !== undefined) || [];
    
    // Find courses to add (new selections that weren't previously selected)
    const coursesToAdd = courseIds.filter(id => !currentCourseIds.includes(id));
    
    // Find courses to remove (previously selected that are no longer selected)
    const coursesToRemove = currentCourseIds.filter(id => !courseIds.includes(id));

    // Process course associations
    this.processCourseAssociations(coursesToAdd, coursesToRemove);
  }

  private processCourseAssociations(coursesToAdd: number[], coursesToRemove: number[]) {
    const observables: any[] = [];

    // Add new course associations
    coursesToAdd.forEach(courseId => {
      const assoc = {
        idCourse: courseId,
        idTeacher: this.selectedTeacherForCourses!.id!
      };
      observables.push(
        this.teacherService.associateTeacherToCourse(assoc)
      );
    });

    // Remove course associations
    coursesToRemove.forEach(courseId => {
      const assoc = {
        idCourse: courseId,
        idTeacher: this.selectedTeacherForCourses!.id!
      };
      observables.push(
        this.teacherService.removeTeacherFromCourse(assoc)
      );
    });

    // Execute all operations
    if (observables.length > 0) {
      forkJoin(observables).subscribe({
        next: () => {
          this.showToast(`Updated course associations: ${coursesToAdd.length} added, ${coursesToRemove.length} removed`);
          this.closeCourseModal();
          this.loadTeachers(true); // Refresh the teacher list
        },
        error: (error) => {
          console.error('Error updating course associations:', error);
          this.showToast('Error updating course associations');
        }
      });
    } else {
      this.closeCourseModal();
    }
  }

  getSelectedCourseIds(): number[] {
    return this.selectedTeacherForCourses?.courses?.map(c => c.id!).filter(id => id !== undefined) || [];
  }

  private async showToast(message: string) {
    console.log('Toast:', message);
  }
}
