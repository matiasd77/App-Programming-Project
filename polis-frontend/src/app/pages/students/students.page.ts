// Students Page Component for Polis University Frontend - Manages student records and course associations

import { Component, OnInit } from '@angular/core'; // Import Angular core Component decorator and OnInit interface
import { CommonModule } from '@angular/common'; // Import common Angular directives like *ngIf, *ngFor
import { Router } from '@angular/router'; // Import Angular router for navigation
import { IonHeader, IonToolbar, IonTitle, IonContent, IonRefresher, IonRefresherContent, IonList, IonItem, IonLabel, IonButton, IonIcon, IonInfiniteScroll, IonInfiniteScrollContent, IonFab, IonFabButton, IonModal, IonButtons } from '@ionic/angular/standalone'; // Import Ionic standalone components
import { FormsModule } from '@angular/forms'; // Import forms module for form handling
import { addIcons } from 'ionicons'; // Import function to register custom icons
import { add, trash, library, home, people, person } from 'ionicons/icons'; // Import specific icons
import { forkJoin } from 'rxjs'; // Import RxJS operator for parallel HTTP requests
import { StudentService } from '../../services/student.service'; // Import student service for API calls
import { CourseService } from '../../services/course.service'; // Import course service for API calls
import { SearchBarComponent } from '../../components/search-bar/search-bar.component'; // Import search bar component
import { StudentFormComponent } from '../../components/student-form/student-form.component'; // Import student form component
import { CourseSelectionComponent } from '../../components/course-selection/course-selection.component'; // Import course selection component
import { StudentDto, SimpleStringFilterDto, Pagination, LongIdDto, CourseDto } from '../../models/dto.types'; // Import data transfer object types
import { environment } from '../../../environments/environment'; // Import environment configuration

// Students page component that manages student records and course associations
@Component({
  selector: 'app-students', // CSS selector to use this component in templates
  templateUrl: './students.page.html', // Path to the component's HTML template
  styleUrls: ['./students.page.scss'], // Path to the component's stylesheet
  standalone: true, // Mark as standalone component (no NgModule needed)
  imports: [CommonModule, FormsModule, SearchBarComponent, StudentFormComponent, CourseSelectionComponent, IonHeader, IonToolbar, IonTitle, IonContent, IonRefresher, IonRefresherContent, IonList, IonItem, IonLabel, IonButton, IonIcon, IonInfiniteScroll, IonInfiniteScrollContent, IonFab, IonFabButton, IonModal, IonButtons] // Import required modules and components
})
export class StudentsPage implements OnInit {
  students: StudentDto[] = []; // Array to store student data
  currentPage = 0; // Current page number for pagination
  hasNextPage = true; // Flag indicating if there are more pages
  isLoading = false; // Flag indicating if data is being loaded
  searchQuery = ''; // Current search query string
  isModalOpen = false; // Flag controlling student form modal visibility
  isCourseModalOpen = false; // Flag controlling course selection modal visibility
  selectedStudent: StudentDto | null = null; // Currently selected student for editing
  selectedStudentForCourses: StudentDto | null = null; // Student selected for course management

  // Constructor for the students page component
  constructor(
    private studentService: StudentService, // Inject student service
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
    this.loadStudents(true); // Load students on component initialization
  }

  // Method to handle search functionality
  onSearch(searchTerm: string) {
    this.searchQuery = searchTerm; // Update search query
    this.loadStudents(true); // Reload students with new search term
  }

  // Method to load students with pagination and search
  loadStudents(reset: boolean = false) {
    if (this.isLoading || (!reset && !this.hasNextPage)) return; // Prevent loading if already loading or no more pages

    if (reset) {
      this.currentPage = 0; // Reset to first page
      this.hasNextPage = true; // Reset pagination flag
      this.students = []; // Clear existing students array
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

    // Call student service to filter students
    this.studentService.filterStudents(filter).subscribe({
      next: (response) => {
        console.log('Students response:', response); // Log response for debugging
        if (response.slice && response.slice.content) {
          console.log('Students data:', response.slice.content); // Log students data
          this.students.push(...response.slice.content); // Add new students to array
          this.hasNextPage = response.slice.hasNext; // Update pagination flag
          this.currentPage++; // Increment page number
        }
        this.isLoading = false; // Clear loading flag
      },
      error: (error) => {
        console.error('Error loading students:', error); // Log error
        this.isLoading = false; // Clear loading flag
      }
    });
  }

  // Method to handle infinite scroll loading
  loadMore(event: any) {
    this.loadStudents(false); // Load next page without reset
    event.target.complete(); // Complete infinite scroll event
  }

  // Method to handle pull-to-refresh
  refresh(event: any) {
    this.loadStudents(true); // Reload students with reset
    event.target.complete(); // Complete refresh event
  }

  // Method to open add student modal
  openAddModal() {
    this.selectedStudent = null; // Clear selected student
    this.isModalOpen = true; // Open modal
  }

  // Method to open edit student modal
  openEditModal(student: StudentDto) {
    this.selectedStudent = { ...student }; // Copy student data to avoid reference issues
    this.isModalOpen = true; // Open modal
  }

  // Method to close student modal
  closeModal() {
    this.isModalOpen = false; // Close modal
    this.selectedStudent = null; // Clear selected student
  }

  // Method called when student is saved
  onStudentSaved() {
    this.closeModal(); // Close modal
    this.loadStudents(true); // Reload students to show updates
  }

  // Method to delete a student
  async deleteStudent(student: StudentDto) {
    if (!student.id) return; // Exit if no student ID

    // For now, just perform the delete without confirmation since AlertController is not available
    if (confirm(`Are you sure you want to delete ${student.firstName} ${student.lastName}?`)) {
      this.performDelete(student.id!); // Perform delete operation
    }
  }

  // Private method to perform actual delete operation
  private performDelete(studentId: number) {
    this.studentService.deleteStudent(studentId).subscribe({
      next: () => {
        this.loadStudents(true); // Reload students after deletion
        this.showToast('Student deleted successfully'); // Show success message
      },
      error: (error) => {
        this.showToast(error.message); // Show error message
      }
    });
  }

  // Course management methods
  // Method to open course selection modal for a student
  openCourseModal(student: StudentDto) {
    console.log('Opening course modal for student:', student); // Log for debugging
    this.selectedStudentForCourses = { ...student }; // Copy student data
    this.isCourseModalOpen = true; // Open course modal
    console.log('Modal should be open:', this.isCourseModalOpen); // Log modal state
  }

  // Method to close course selection modal
  closeCourseModal() {
    console.log('Closing course modal'); // Log for debugging
    this.isCourseModalOpen = false; // Close course modal
    this.selectedStudentForCourses = null; // Clear selected student
  }

  // Method to handle course selection from modal
  onCoursesSelected(courseIds: number[]) {
    console.log('Students page received course selection:', courseIds); // Log course selection
    console.log('Selected student:', this.selectedStudentForCourses); // Log selected student
    
    if (!this.selectedStudentForCourses?.id) {
      console.error('No student selected for courses'); // Log error
      return;
    }

    // Since backend only supports one course per student, we'll handle the first selected course
    const selectedCourseId = courseIds.length > 0 ? courseIds[0] : null; // Get first selected course ID
    const currentCourseId = this.selectedStudentForCourses.course?.id; // Get current course ID

    if (selectedCourseId && selectedCourseId !== currentCourseId) {
      // Add new course association
      this.processCourseAssociations([selectedCourseId], []); // Process course addition
    } else if (!selectedCourseId && currentCourseId) {
      // Remove current course association
      this.processCourseAssociations([], [currentCourseId]); // Process course removal
    } else {
      // No changes needed
      this.closeCourseModal(); // Close modal
    }
  }

  // Private method to process course associations (add/remove)
  private processCourseAssociations(coursesToAdd: number[], coursesToRemove: number[]) {
    const observables: any[] = []; // Array to store HTTP request observables

    // Add new course associations
    coursesToAdd.forEach(courseId => {
      const assoc = {
        idCourse: courseId, // Course ID to associate
        idStudent: this.selectedStudentForCourses!.id! // Student ID
      };
      observables.push(
        this.studentService.associateStudentToCourse(assoc) // Add association request
      );
    });

    // Remove course associations
    coursesToRemove.forEach(courseId => {
      const assoc = {
        idCourse: courseId, // Course ID to remove
        idStudent: this.selectedStudentForCourses!.id! // Student ID
      };
      observables.push(
        this.studentService.removeStudentFromCourse(assoc) // Remove association request
      );
    });

    // Execute all operations in parallel
    if (observables.length > 0) {
      forkJoin(observables).subscribe({
        next: () => {
          this.showToast(`Updated course associations: ${coursesToAdd.length} added, ${coursesToRemove.length} removed`); // Show success message
          this.closeCourseModal(); // Close modal
          // Force a complete refresh of the student list to get updated course data
          setTimeout(() => {
            this.loadStudents(true); // Reload students after delay
          }, 500); // Small delay to ensure backend has processed the changes
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

  // Method to get currently selected course IDs for a student
  getSelectedCourseIds(): number[] {
    const ids = this.selectedStudentForCourses?.course ? [this.selectedStudentForCourses.course.id!] : []; // Get course ID if exists
    console.log('Getting selected course IDs:', ids); // Log for debugging
    return ids; // Return array of course IDs
  }

  // Private method to show toast messages (placeholder for now)
  private async showToast(message: string) {
    // For now, just log the message since ToastController is not available in standalone
    console.log('Toast:', message); // Log message to console
  }
}
