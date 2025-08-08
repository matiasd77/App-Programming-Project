# Polis Android App

A native Android application for the Polis Student Management System, built with Kotlin and Material Design.

## Features

### ✅ Fully Implemented
- **Student Management**: Complete CRUD operations with infinite scroll, search, and pull-to-refresh
- **Teacher Management**: Complete CRUD operations with infinite scroll, search, and pull-to-refresh
- **Course Management**: Complete CRUD operations with infinite scroll, search, and pull-to-refresh
- **API Integration**: Full Retrofit implementation for all endpoints
- **Error Handling**: Proper error parsing and user-friendly messages
- **Search & Pagination**: Debounced search with backend pagination
- **Pull-to-Refresh**: SwipeRefreshLayout for data reloading
- **Loading States**: Progress indicators and empty states
- **Form Validation**: Client-side validation with error messages
- **Material Design**: Modern UI with Material Design 3 components

### 🚧 Planned (Advanced Features)
- **Association Management**: UI for assigning students to courses and teachers to courses
- **Advanced Search**: Filtering and sorting options
- **Offline Support**: Caching and offline data access
- **Push Notifications**: Real-time updates
- **Dark Mode Toggle**: User preference for theme switching

## Technical Stack

- **Language**: Kotlin
- **Architecture**: MVVM with Activities
- **UI Framework**: Material Design 3
- **Networking**: Retrofit 2 with OkHttp
- **JSON Parsing**: Gson
- **Async Programming**: Kotlin Coroutines
- **View Binding**: Android View Binding
- **Layout**: ConstraintLayout and CoordinatorLayout

## Project Structure

```
app/src/main/java/com/polis/app/
├── api/
│   ├── RetrofitClient.kt          # Retrofit configuration
│   ├── StudentService.kt          # Student API endpoints
│   ├── TeacherService.kt          # Teacher API endpoints
│   └── CourseService.kt           # Course API endpoints
├── model/
│   ├── StudentDto.kt              # Student data model
│   ├── TeacherDto.kt              # Teacher data model
│   ├── CourseDto.kt               # Course data model
│   ├── Pagination.kt              # Pagination model
│   ├── RespSliceDto.kt            # Paginated response model
│   ├── RespSingleDto.kt           # Single response model
│   ├── ServerStatus.kt            # Error status model
│   └── ServerErrorEnum.kt         # Error codes enum
├── ui/
│   ├── MainActivity.kt            # Main activity with navigation
│   ├── StudentListActivity.kt     # Student list with CRUD
│   ├── StudentFormActivity.kt     # Student add/edit form
│   ├── TeacherListActivity.kt     # Teacher list with CRUD
│   ├── TeacherFormActivity.kt     # Teacher add/edit form
│   ├── CourseListActivity.kt      # Course list with CRUD
│   └── CourseFormActivity.kt      # Course add/edit form
└── adapter/
    ├── StudentAdapter.kt          # RecyclerView adapter for students
    ├── TeacherAdapter.kt          # RecyclerView adapter for teachers
    └── CourseAdapter.kt           # RecyclerView adapter for courses
```

## Setup Instructions

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24+ (API level 24)
- Java 8 or later
- Backend server running on `http://localhost:8080`

### Build and Run
1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Connect an Android device or start an emulator
5. Run the app

### Backend Connection
The app is configured to connect to the backend at `http://10.0.2.2:8080` (Android emulator localhost).
For physical devices, update the BASE_URL in `RetrofitClient.kt` to your server's IP address.

## API Integration

### Student Endpoints
- `POST /upsertStudent` - Create or update student
- `POST /filterStudents` - Search and paginate students
- `POST /deleteStudent` - Delete student
- `POST /getStudent` - Get single student
- `POST /associateStudentToCourse` - Associate student to course
- `POST /removeStudentFromCourse` - Remove student from course

### Teacher Endpoints
- `POST /upsertTeacher` - Create or update teacher
- `POST /filterTeachers` - Search and paginate teachers
- `POST /deleteTeacher` - Delete teacher
- `POST /getTeacher` - Get single teacher

### Course Endpoints
- `POST /upsertCourse` - Create or update course
- `POST /filterCourses` - Search and paginate courses
- `POST /deleteCourse` - Delete course
- `POST /getCourse` - Get single course
- `POST /associateTeacherToCourse` - Associate teacher to course
- `POST /removeTeacherFromCourse` - Remove teacher from course

### Error Handling
The app handles backend errors gracefully:
- `DELETE_STUDENT_NOT_ALLOWED` - Cannot delete student with course
- `DELETE_TEACHER_NOT_ALLOWED` - Cannot delete teacher with courses
- `DELETE_COURSE_NOT_ALLOWED` - Cannot delete course with students
- `STUDENT_NOT_FOUND` / `TEACHER_NOT_FOUND` / `COURSE_NOT_FOUND` - Entity not found
- `VALIDATION_ERROR` - Form validation errors
- Network errors with user-friendly messages

## UI/UX Features

### Material Design 3
- Modern Material Design components
- Consistent color scheme and typography
- Responsive layout for phones and tablets
- Dark mode support (automatic)

### Navigation
- Bottom navigation with Students, Teachers, and Courses
- Toolbar with back navigation
- Floating Action Button for adding new items

### User Experience
- Infinite scroll pagination
- Debounced search (500ms delay)
- Pull-to-refresh functionality
- Loading indicators and empty states
- Confirmation dialogs for delete operations
- Form validation with error messages
- Responsive layouts for different screen sizes

## Dependencies

```gradle
// Core Android
implementation 'androidx.core:core-ktx:1.12.0'
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'

// Material Design
implementation 'com.google.android.material:material:1.11.0'

// Navigation
implementation 'androidx.navigation:navigation-fragment-ktx:2.7.6'
implementation 'androidx.navigation:navigation-ui-ktx:2.7.6'

// Networking
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'

// JSON Parsing
implementation 'com.google.code.gson:gson:2.10.1'

// Coroutines
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3'

// ViewModel and LiveData
implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'
implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.7.0'
```

## Next Steps

1. **Association Management**: Add UI for managing student-course and teacher-course associations
2. **Enhanced Features**: Add sorting, filtering, and advanced search options
3. **Testing**: Add unit tests and UI tests
4. **Performance**: Implement caching and offline support
5. **Advanced UI**: Add animations, transitions, and enhanced user experience
6. **Security**: Implement proper authentication and authorization

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is part of the Polis Student Management System.
