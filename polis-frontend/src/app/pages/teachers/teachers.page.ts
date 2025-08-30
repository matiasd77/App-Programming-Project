// Teachers Page Component for Polis University Frontend - Manages teacher records and course associations

import { Component, OnInit } from '@angular/core'; // Import Angular core Component decorator and OnInit interface
import { CommonModule } from '@angular/common'; // Import common Angular directives like *ngIf, *ngFor
import { Router } from '@angular/router'; // Import Angular router for navigation
import { IonHeader, IonToolbar, IonTitle, IonContent, IonRefresher, IonRefresherContent, IonList, IonItem, IonLabel, IonButton, IonIcon, IonInfiniteScroll, IonInfiniteScrollContent, IonFab, IonFabButton, IonModal, IonButtons } from '@ionic/angular/standalone'; // Import Ionic standalone components
import { FormsModule } from '@angular/forms'; // Import forms module for form handling
import { addIcons } from 'ionicons'; // Import function to register custom icons
import { add, trash, library, home, people, person } from 'ionicons/icons'; // Import specific icons
import { forkJoin } from 'rxjs'; // Import RxJS operator for parallel HTTP requests
import { TeacherService } from '../../services/teacher.service'; // Import teacher service for API calls
import { CourseService } from '../../services/course.service'; // Import course service for API calls
import { SearchBarComponent } from '../../components/search-bar/search-bar.component'; // Import search bar component
import { TeacherFormComponent } from '../../components/teacher-form/teacher-form.component'; // Import teacher form component
import { CourseSelectionComponent } from '../../components/course-selection/course-selection.component'; // Import course selection component
import { TeacherDto, SimpleStringFilterDto, Pagination, LongIdDto, CourseDto } from '../../models/dto.types'; // Import data transfer object types
import { environment } from '../../../environments/environment'; // Import environment configuration

// Teachers page component that manages teacher records and course associations
@Component({
  selector: 'app-teachers', // CSS selector to use this component in templates
  templateUrl: './teachers.page.html', // Path to the component's HTML template
  styleUrls: ['./teachers.page.scss'], // Path to the component's stylesheet
  standalone: true, // Mark as standalone component (no NgModule needed)
  imports: [CommonModule, FormsModule, SearchBarComponent, TeacherFormComponent, CourseSelectionComponent, IonHeader, IonToolbar, IonTitle, IonContent, IonRefresher, IonRefresherContent, IonList, IonItem, IonLabel, IonButton, IonIcon, IonInfiniteScroll, IonInfiniteScrollContent, IonFab, IonFabButton, IonModal, IonButtons] // Import required modules and components
})
export class TeachersPage implements OnInit {
  teachers: TeacherDto[] = []; // Array to store teacher data
  currentPage = 0; // Current page number for pagination
  hasNextPage = true; // Flag indicating if there are more pages
  isLoading = false; // Flag indicating if data is being loaded
  searchQuery = ''; // Current search query string
  isModalOpen = false; // Flag controlling teacher form modal visibility
  isCourseModalOpen = false; // Flag controlling course selection modal visibility
  selectedTeacher: TeacherDto | null = null; // Currently selected teacher for editing
  selectedTeacherForCourses: TeacherDto | null = null; // Teacher selected for course management

  // Constructor for the teachers page component
  constructor(
    private teacherService: TeacherService, // Inject teacher service
    private courseService: CourseService, // Inject course service
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
    this.loadTeachers(true); // Load teachers on component initialization
  }

  // Method to handle search functionality
  onSearch(searchTerm: string) {
    this.searchQuery = searchTerm; // Update search query
    this.loadTeachers(true); // Reload teachers with new search term
  }

  // Method to load teachers with pagination and search
  loadTeachers(reset: boolean = false) {
    if (this.isLoading || (!reset && !this.hasNextPage)) return; // Prevent loading if already loading or no more pages

    if (reset) {
      this.currentPage = 0; // Reset to first page
      this.hasNextPage = true; // Reset pagination flag
      this.teachers = []; // Clear existing teachers array
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

    // Call teacher service to filter teachers
    this.teacherService.filterTeachers(filter).subscribe({
      next: (response) => {
        if (response.slice && response.slice.content) {
          this.teachers.push(...response.slice.content); // Add new teachers to array
          this.hasNextPage = response.slice.hasNext; // Update pagination flag
          this.currentPage++; // Increment page number
        }
        this.isLoading = false; // Clear loading flag
      },
      error: (error) => {
        console.error('Error loading teachers:', error); // Log error
        this.isLoading = false; // Clear loading flag
      }
    });
  }

  // Method to handle infinite scroll loading
  loadMore(event: any) {
    this.loadTeachers(false); // Load next page without reset
    event.target.complete(); // Complete infinite scroll event
  }

  // Method to handle pull-to-refresh
  refresh(event: any) {
    this.loadTeachers(true); // Reload teachers with reset
    event.target.complete(); // Complete refresh event
  }

  // Method to open add teacher modal
  openAddModal() {
    this.selectedTeacher = null; // Clear selected teacher
    this.isModalOpen = true; // Open modal
  }

  // Method to open edit teacher modal
  openEditModal(teacher: TeacherDto) {
    this.selectedTeacher = { ...teacher }; // Copy teacher data to avoid reference issues
    this.isModalOpen = true; // Open modal
  }

  // Method to close teacher modal
  closeModal() {
    this.isModalOpen = false; // Close modal
    this.selectedTeacher = null; // Clear selected teacher
  }

  // Method called when teacher is saved
  onTeacherSaved() {
    this.closeModal(); // Close modal
    this.loadTeachers(true); // Reload teachers to show updates
  }

  // Method to delete a teacher
  async deleteTeacher(teacher: TeacherDto) {
    if (!teacher.id) return; // Exit if no teacher ID

    if (confirm(`Are you sure you want to delete ${teacher.firstName} ${teacher.lastName}?`)) {
      this.performDelete(teacher.id!); // Perform delete operation
    }
  }

  // Private method to perform actual delete operation
  private performDelete(teacherId: number) {
    this.teacherService.deleteTeacher(teacherId).subscribe({
      next: () => {
        this.loadTeachers(true); // Reload teachers after deletion
        this.showToast('Teacher deleted successfully'); // Show success message
      },
      error: (error) => {
        this.showToast(error.message); // Show error message
      }
    });
  }

  // Course management methods
  // Method to open course selection modal for a teacher
  openCourseModal(teacher: TeacherDto) {
    this.selectedTeacherForCourses = { ...teacher }; // Copy teacher data
    this.isCourseModalOpen = true; // Open course modal
  }

  // Method to close course selection modal
  closeCourseModal() {
    this.isCourseModalOpen = false; // Close course modal
    this.selectedTeacherForCourses = null; // Clear selected teacher
  }

  // Method to handle course selection from modal
  onCoursesSelected(courseIds: number[]) {
    if (!this.selectedTeacherForCourses?.id) return; // Exit if no teacher selected

    // Get current course associations
    const currentCourseIds = this.selectedTeacherForCourses.courses?.map(c => c.id!).filter(id => id !== undefined) || []; // Extract current course IDs
    
    // Find courses to add (new selections that weren't previously selected)
    const coursesToAdd = courseIds.filter(id => !currentCourseIds.includes(id)); // Filter new course selections
    
    // Find courses to remove (previously selected that are no longer selected)
    const coursesToRemove = currentCourseIds.filter(id => !courseIds.includes(id)); // Filter courses to remove

    // Process course associations
    this.processCourseAssociations(coursesToAdd, coursesToRemove); // Process the changes
  }

  // Private method to process course associations (add/remove)
  private processCourseAssociations(coursesToAdd: number[], coursesToRemove: number[]) {
    const observables: any[] = []; // Array to store HTTP request observables

    // Add new course associations
    coursesToAdd.forEach(courseId => {
      const assoc = {
        idCourse: courseId, // Course ID to associate
        idTeacher: this.selectedTeacherForCourses!.id! // Teacher ID
      };
      observables.push(
        this.teacherService.associateTeacherToCourse(assoc) // Add association request
      );
    });

    // Remove course associations
    coursesToRemove.forEach(courseId => {
      const assoc = {
        idCourse: courseId, // Course ID to remove
        idTeacher: this.selectedTeacherForCourses!.id! // Teacher ID
      };
      observables.push(
        this.teacherService.removeTeacherFromCourse(assoc) // Remove association request
      );
    });

    // Execute all operations in parallel
    if (observables.length > 0) {
      forkJoin(observables).subscribe({
        next: () => {
          this.showToast(`Updated course associations: ${coursesToAdd.length} added, ${coursesToRemove.length} removed`); // Show success message
          this.closeCourseModal(); // Close modal
          this.loadTeachers(true); // Refresh the teacher list
        },
        error: (error) => {
          console.error('Error updating course associations:', error); // Log error
          this.showToast('Error updating course associations'); // Show error message
        }
      });
    } else {
      this.closeCourseModal(); // Close modal if no operations
    }
  }

  // Method to get currently selected course IDs for a teacher
  getSelectedCourseIds(): number[] {
    return this.selectedTeacherForCourses?.courses?.map(c => c.id!).filter(id => id !== undefined) || []; // Return array of course IDs
  }

  // Private method to show toast messages (placeholder for now)
  private async showToast(message: string) {
    console.log('Toast:', message); // Log message to console
  }
}
