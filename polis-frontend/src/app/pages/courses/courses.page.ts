import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { IonHeader, IonToolbar, IonTitle, IonContent, IonRefresher, IonRefresherContent, IonList, IonItem, IonLabel, IonButton, IonIcon, IonInfiniteScroll, IonInfiniteScrollContent, IonFab, IonFabButton, IonModal, IonButtons } from '@ionic/angular/standalone';
import { FormsModule } from '@angular/forms';
import { addIcons } from 'ionicons';
import { add, trash, people, person, home, library } from 'ionicons/icons';
import { CourseService } from '../../services/course.service';
import { TeacherService } from '../../services/teacher.service';
import { StudentService } from '../../services/student.service';
import { SearchBarComponent } from '../../components/search-bar/search-bar.component';
import { CourseFormComponent } from '../../components/course-form/course-form.component';
import { CourseDto, SimpleStringFilterDto, Pagination, LongIdDto } from '../../models/dto.types';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-courses',
  templateUrl: './courses.page.html',
  styleUrls: ['./courses.page.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, SearchBarComponent, CourseFormComponent, IonHeader, IonToolbar, IonTitle, IonContent, IonRefresher, IonRefresherContent, IonList, IonItem, IonLabel, IonButton, IonIcon, IonInfiniteScroll, IonInfiniteScrollContent, IonFab, IonFabButton, IonModal, IonButtons]
})
export class CoursesPage implements OnInit {
  courses: CourseDto[] = [];
  currentPage = 0;
  hasNextPage = true;
  isLoading = false;
  searchQuery = '';
  isModalOpen = false;
  selectedCourse: CourseDto | null = null;

  constructor(
    private courseService: CourseService,
    private teacherService: TeacherService,
    private studentService: StudentService,
    private router: Router
  ) {
    addIcons({home,people,person,library,trash,add});
  }

  navigateTo(page: string) {
    this.router.navigate(['/tabs', page]);
  }

  ngOnInit() {
    this.loadCourses(true);
  }

  onSearch(searchTerm: string) {
    this.searchQuery = searchTerm;
    this.loadCourses(true);
  }

  loadCourses(reset: boolean = false) {
    if (this.isLoading || (!reset && !this.hasNextPage)) return;

    if (reset) {
      this.currentPage = 0;
      this.hasNextPage = true;
      this.courses = [];
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

    this.courseService.filterCourses(filter).subscribe({
      next: (response) => {
        if (response.slice && response.slice.content) {
          this.courses.push(...response.slice.content);
          this.hasNextPage = response.slice.hasNext;
          this.currentPage++;
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading courses:', error);
        this.isLoading = false;
      }
    });
  }

  loadMore(event: any) {
    this.loadCourses(false);
    event.target.complete();
  }

  refresh(event: any) {
    this.loadCourses(true);
    event.target.complete();
  }

  openAddModal() {
    this.selectedCourse = null;
    this.isModalOpen = true;
  }

  openEditModal(course: CourseDto) {
    this.selectedCourse = { ...course };
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
    this.selectedCourse = null;
  }

  onCourseSaved() {
    this.closeModal();
    this.loadCourses(true);
  }

  async deleteCourse(course: CourseDto) {
    if (!course.id) return;

    if (confirm(`Are you sure you want to delete ${course.title}?`)) {
      this.performDelete(course.id!);
    }
  }

  private performDelete(courseId: number) {
    this.courseService.deleteCourse(courseId).subscribe({
      next: () => {
        this.loadCourses(true);
        this.showToast('Course deleted successfully');
      },
      error: (error) => {
        this.showToast(error.message);
      }
    });
  }

  private async showToast(message: string) {
    console.log('Toast:', message);
  }
}
