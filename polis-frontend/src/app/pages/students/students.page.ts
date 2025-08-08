import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { IonHeader, IonToolbar, IonTitle, IonContent, IonRefresher, IonRefresherContent, IonList, IonItem, IonLabel, IonButton, IonIcon, IonInfiniteScroll, IonInfiniteScrollContent, IonFab, IonFabButton, IonModal, IonButtons } from '@ionic/angular/standalone';
import { FormsModule } from '@angular/forms';
import { addIcons } from 'ionicons';
import { add, trash, library, home, people, person } from 'ionicons/icons';
import { forkJoin } from 'rxjs';
import { StudentService } from '../../services/student.service';
import { CourseService } from '../../services/course.service';
import { SearchBarComponent } from '../../components/search-bar/search-bar.component';
import { StudentFormComponent } from '../../components/student-form/student-form.component';
import { CourseSelectionComponent } from '../../components/course-selection/course-selection.component';
import { StudentDto, SimpleStringFilterDto, Pagination, LongIdDto, CourseDto } from '../../models/dto.types';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-students',
  templateUrl: './students.page.html',
  styleUrls: ['./students.page.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, SearchBarComponent, StudentFormComponent, CourseSelectionComponent, IonHeader, IonToolbar, IonTitle, IonContent, IonRefresher, IonRefresherContent, IonList, IonItem, IonLabel, IonButton, IonIcon, IonInfiniteScroll, IonInfiniteScrollContent, IonFab, IonFabButton, IonModal, IonButtons]
})
export class StudentsPage implements OnInit {
  students: StudentDto[] = [];
  currentPage = 0;
  hasNextPage = true;
  isLoading = false;
  searchQuery = '';
  isModalOpen = false;
  isCourseModalOpen = false;
  selectedStudent: StudentDto | null = null;
  selectedStudentForCourses: StudentDto | null = null;

  constructor(
    private studentService: StudentService,
    private courseService: CourseService,
    private router: Router
  ) {
    addIcons({home,people,person,library,trash,add});
  }

  navigateTo(page: string) {
    this.router.navigate(['/tabs', page]);
  }

  ngOnInit() {
    this.loadStudents(true);
  }

  onSearch(searchTerm: string) {
    this.searchQuery = searchTerm;
    this.loadStudents(true);
  }

  loadStudents(reset: boolean = false) {
    if (this.isLoading || (!reset && !this.hasNextPage)) return;

    if (reset) {
      this.currentPage = 0;
      this.hasNextPage = true;
      this.students = [];
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

    this.studentService.filterStudents(filter).subscribe({
      next: (response) => {
        console.log('Students response:', response);
        if (response.slice && response.slice.content) {
          console.log('Students data:', response.slice.content);
          this.students.push(...response.slice.content);
          this.hasNextPage = response.slice.hasNext;
          this.currentPage++;
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading students:', error);
        this.isLoading = false;
      }
    });
  }

  loadMore(event: any) {
    this.loadStudents(false);
    event.target.complete();
  }

  refresh(event: any) {
    this.loadStudents(true);
    event.target.complete();
  }

  openAddModal() {
    this.selectedStudent = null;
    this.isModalOpen = true;
  }

  openEditModal(student: StudentDto) {
    this.selectedStudent = { ...student };
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
    this.selectedStudent = null;
  }

  onStudentSaved() {
    this.closeModal();
    this.loadStudents(true);
  }

  async deleteStudent(student: StudentDto) {
    if (!student.id) return;

    // For now, just perform the delete without confirmation since AlertController is not available
    if (confirm(`Are you sure you want to delete ${student.firstName} ${student.lastName}?`)) {
      this.performDelete(student.id!);
    }
  }

  private performDelete(studentId: number) {
    const request: LongIdDto = { id: studentId };
    
    this.studentService.deleteStudent(request).subscribe({
      next: () => {
        this.loadStudents(true);
        this.showToast('Student deleted successfully');
      },
      error: (error) => {
        this.showToast(error.message);
      }
    });
  }

  // Course management methods
  openCourseModal(student: StudentDto) {
    console.log('Opening course modal for student:', student);
    this.selectedStudentForCourses = { ...student };
    this.isCourseModalOpen = true;
    console.log('Modal should be open:', this.isCourseModalOpen);
  }

  closeCourseModal() {
    console.log('Closing course modal');
    this.isCourseModalOpen = false;
    this.selectedStudentForCourses = null;
  }

  onCoursesSelected(courseIds: number[]) {
    console.log('Students page received course selection:', courseIds);
    console.log('Selected student:', this.selectedStudentForCourses);
    
    if (!this.selectedStudentForCourses?.id) {
      console.error('No student selected for courses');
      return;
    }

    // Since backend only supports one course per student, we'll handle the first selected course
    const selectedCourseId = courseIds.length > 0 ? courseIds[0] : null;
    const currentCourseId = this.selectedStudentForCourses.course?.id;

    if (selectedCourseId && selectedCourseId !== currentCourseId) {
      // Add new course association
      this.processCourseAssociations([selectedCourseId], []);
    } else if (!selectedCourseId && currentCourseId) {
      // Remove current course association
      this.processCourseAssociations([], [currentCourseId]);
    } else {
      // No changes needed
      this.closeCourseModal();
    }
  }

  private processCourseAssociations(coursesToAdd: number[], coursesToRemove: number[]) {
    const observables: any[] = [];

    // Add new course associations
    coursesToAdd.forEach(courseId => {
      const assoc = {
        idCourse: courseId,
        idStudent: this.selectedStudentForCourses!.id!
      };
      observables.push(
        this.studentService.associateStudentToCourse(assoc)
      );
    });

    // Remove course associations
    coursesToRemove.forEach(courseId => {
      const assoc = {
        idCourse: courseId,
        idStudent: this.selectedStudentForCourses!.id!
      };
      observables.push(
        this.studentService.removeStudentFromCourse(assoc)
      );
    });

    // Execute all operations
    if (observables.length > 0) {
      forkJoin(observables).subscribe({
        next: () => {
          this.showToast(`Updated course associations: ${coursesToAdd.length} added, ${coursesToRemove.length} removed`);
          this.closeCourseModal();
          // Force a complete refresh of the student list to get updated course data
          setTimeout(() => {
            this.loadStudents(true);
          }, 500); // Small delay to ensure backend has processed the changes
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
    const ids = this.selectedStudentForCourses?.course ? [this.selectedStudentForCourses.course.id!] : [];
    console.log('Getting selected course IDs:', ids);
    return ids;
  }

  private async showToast(message: string) {
    // For now, just log the message since ToastController is not available in standalone
    console.log('Toast:', message);
  }




}
