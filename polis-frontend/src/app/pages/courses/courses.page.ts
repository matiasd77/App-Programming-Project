// Courses Page Component for Polis University Frontend - Manages course records and information

import { Component, OnInit } from '@angular/core'; // Import Angular core Component decorator and OnInit interface
import { CommonModule } from '@angular/common'; // Import common Angular directives like *ngIf, *ngFor
import { Router } from '@angular/router'; // Import Angular router for navigation
import { IonHeader, IonToolbar, IonTitle, IonContent, IonRefresher, IonRefresherContent, IonList, IonItem, IonLabel, IonButton, IonIcon, IonInfiniteScroll, IonInfiniteScrollContent, IonFab, IonFabButton, IonModal, IonButtons } from '@ionic/angular/standalone'; // Import Ionic standalone components
import { FormsModule } from '@angular/forms'; // Import forms module for form handling
import { addIcons } from 'ionicons'; // Import function to register custom icons
import { add, trash, people, person, home, library } from 'ionicons/icons'; // Import specific icons
import { CourseService } from '../../services/course.service'; // Import course service for API calls
import { TeacherService } from '../../services/teacher.service'; // Import teacher service for API calls
import { StudentService } from '../../services/student.service'; // Import student service for API calls
import { SearchBarComponent } from '../../components/search-bar/search-bar.component'; // Import search bar component
import { CourseFormComponent } from '../../components/course-form/course-form.component'; // Import course form component
import { CourseDto, SimpleStringFilterDto, Pagination, LongIdDto } from '../../models/dto.types'; // Import data transfer object types
import { environment } from '../../../environments/environment'; // Import environment configuration

// Courses page component that manages course records and information
@Component({
  selector: 'app-courses', // CSS selector to use this component in templates
  templateUrl: './courses.page.html', // Path to the component's HTML template
  styleUrls: ['./courses.page.scss'], // Path to the component's stylesheet
  standalone: true, // Mark as standalone component (no NgModule needed)
  imports: [CommonModule, FormsModule, SearchBarComponent, CourseFormComponent, IonHeader, IonToolbar, IonTitle, IonContent, IonRefresher, IonRefresherContent, IonList, IonItem, IonLabel, IonButton, IonIcon, IonInfiniteScroll, IonInfiniteScrollContent, IonFab, IonFabButton, IonModal, IonButtons] // Import required modules and components
})
export class CoursesPage implements OnInit {
  courses: CourseDto[] = []; // Array to store course data
  currentPage = 0; // Current page number for pagination
  hasNextPage = true; // Flag indicating if there are more pages
  isLoading = false; // Flag indicating if data is being loaded
  searchQuery = ''; // Current search query string
  isModalOpen = false; // Flag controlling course form modal visibility
  selectedCourse: CourseDto | null = null; // Currently selected course for editing

  // Constructor for the courses page component
  constructor(
    private courseService: CourseService, // Inject course service
    private teacherService: TeacherService, // Inject teacher service
    private studentService: StudentService, // Inject student service
    private router: Router // Inject Angular router service
  ) {
    addIcons({home,people,person,library,trash,add}); // Register custom icons for use in templates
  }

  // Method to navigate to different tabs/pages
  navigateTo(page: string) {
    this.router.navigate(['/tabs', page]); // Navigate to specified tab using router
  }

  // Lifecycle hook called when component initializes
  ngOnInit() {
    this.loadCourses(true); // Load courses on component initialization
  }

  // Method to handle search functionality
  onSearch(searchTerm: string) {
    this.searchQuery = searchTerm; // Update search query
    this.loadCourses(true); // Reload courses with new search term
  }

  // Method to load courses with pagination and search
  loadCourses(reset: boolean = false) {
    if (this.isLoading || (!reset && !this.hasNextPage)) return; // Prevent loading if already loading or no more pages

    if (reset) {
      this.currentPage = 0; // Reset to first page
      this.hasNextPage = true; // Reset pagination flag
      this.courses = []; // Clear existing courses array
    }

    this.isLoading = true; // Set loading flag

    // Create pagination object
    const pagination: Pagination = {
      pageNumber: this.currentPage, // Current page number
      pageSize: environment.pageSize // Page size from environment config
    };

    // Create filter object with search query and pagination
    const filter: SimpleStringFilterDto = {
      filter: this.searchQuery, // Search query string
      pagination: pagination // Pagination configuration
    };

    // Call course service to filter courses
    this.courseService.filterCourses(filter).subscribe({
      next: (response) => {
        if (response.slice && response.slice.content) {
          this.courses.push(...response.slice.content); // Add new courses to array
          this.hasNextPage = response.slice.hasNext; // Update pagination flag
          this.currentPage++; // Increment page number
        }
        this.isLoading = false; // Clear loading flag
      },
      error: (error) => {
        console.error('Error loading courses:', error); // Log error
        this.isLoading = false; // Clear loading flag
      }
    });
  }

  // Method to handle infinite scroll loading
  loadMore(event: any) {
    this.loadCourses(false); // Load next page without reset
    event.target.complete(); // Complete infinite scroll event
  }

  // Method to handle pull-to-refresh
  refresh(event: any) {
    this.loadCourses(true); // Reload courses with reset
    event.target.complete(); // Complete refresh event
  }

  // Method to open add course modal
  openAddModal() {
    this.selectedCourse = null; // Clear selected course
    this.isModalOpen = true; // Open modal
  }

  // Method to open edit course modal
  openEditModal(course: CourseDto) {
    this.selectedCourse = { ...course }; // Copy course data to avoid reference issues
    this.isModalOpen = true; // Open modal
  }

  // Method to close course modal
  closeModal() {
    this.isModalOpen = false; // Close modal
    this.selectedCourse = null; // Clear selected course
  }

  // Method called when course is saved
  onCourseSaved() {
    this.closeModal(); // Close modal
    this.loadCourses(true); // Reload courses to show updates
  }

  // Method to delete a course
  async deleteCourse(course: CourseDto) {
    if (!course.id) return; // Exit if no course ID

    if (confirm(`Are you sure you want to delete ${course.title}?`)) {
      this.performDelete(course.id!); // Perform delete operation
    }
  }

  // Private method to perform actual delete operation
  private performDelete(courseId: number) {
    this.courseService.deleteCourse(courseId).subscribe({
      next: () => {
        this.loadCourses(true); // Reload courses after deletion
        this.showToast('Course deleted successfully'); // Show success message
      },
      error: (error) => {
        this.showToast(error.message); // Show error message
      }
    });
  }

  // Private method to show toast messages (placeholder for now)
  private async showToast(message: string) {
    console.log('Toast:', message); // Log message to console
  }
}
