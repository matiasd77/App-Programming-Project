# Polis University Android App

A native Android application for the Polis University Management System, built with modern Android development practices.

## 🚀 Features

### ✅ Implemented
- **Students CRUD Operations**
  - List all students with pull-to-refresh
  - Add new students with form validation
  - Edit existing students
  - Delete students with confirmation dialog
  - Search and filter students
  - Error handling with user-friendly messages
  - Loading states and progress indicators

### 🔄 Placeholder (Ready for Implementation)
- **Teachers Management** - Following the same pattern as Students
- **Courses Management** - Following the same pattern as Students

## 🛠 Tech Stack

- **Language**: Kotlin
- **Minimum SDK**: 24 (Android 7.0)
- **Architecture**: MVVM (Model-View-ViewModel)
- **UI Framework**: Jetpack Compose
- **Networking**: Retrofit + Gson
- **Async Programming**: Coroutines + Flow
- **Dependency Injection**: Hilt
- **Navigation**: Compose Navigation
- **Design System**: Material 3

## 📱 App Structure

```
app/src/main/java/com/polis/university/
├── data/
│   ├── api/           # Retrofit API service interfaces
│   ├── dto/           # Data Transfer Objects
│   └── repository/    # Repository classes
├── di/                # Hilt dependency injection modules
├── ui/
│   ├── screens/       # Compose UI screens
│   └── theme/         # App theme and styling
├── viewmodel/         # ViewModels for state management
└── MainActivity.kt    # Main activity with navigation
```

## 🔧 Setup Instructions

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 17 or later
- Android SDK 34
- Spring Boot backend running on `http://10.0.2.2:8080` (Android emulator)

### Running the App
1. **Open in Android Studio**
   - Open the `polis-android` folder in Android Studio
   - Wait for Gradle sync to complete

2. **Configure Backend**
   - Ensure your Spring Boot backend is running on port 8080
   - For Android emulator, use `http://10.0.2.2:8080`
   - For physical device, use your computer's IP address

3. **Build and Run**
   - Connect an Android device or start an emulator
   - Click the "Run" button in Android Studio
   - The app will install and launch automatically

## 🎯 How to Extend

### Adding Teachers Management
1. **Create Teacher DTOs** (already in `dto.types.ts`)
2. **Implement TeacherRepository** (placeholder exists)
3. **Create TeacherListViewModel** and `TeacherFormViewModel`
4. **Create TeacherListScreen** and `TeacherFormScreen`
5. **Update navigation** in `MainActivity`

### Adding Courses Management
1. **Create Course DTOs** (already in `dto.types.ts`)
2. **Implement CourseRepository** (placeholder exists)
3. **Create CourseListViewModel** and `CourseFormViewModel`
4. **Create CourseListScreen** and `CourseFormScreen`
5. **Update navigation** in `MainActivity`

### Pattern to Follow
The Students implementation serves as a template:
- **Repository Pattern**: Handle data operations
- **ViewModel Pattern**: Manage UI state and business logic
- **Compose UI**: Create screens with Material 3 design
- **Navigation**: Add routes to the navigation graph

## 🔌 API Integration

The app connects to your Spring Boot backend with these endpoints:

### Students
- `POST /student/filter` - List students with pagination
- `POST /student/get` - Get single student
- `POST /student/upsert` - Create/Update student
- `DELETE /student/{id}` - Delete student

### Configuration
- Base URL: `http://10.0.2.2:8080/` (emulator)
- Network permissions already configured
- CORS support for local development

## 🎨 UI/UX Features

- **Material 3 Design**: Modern, accessible interface
- **Responsive Layout**: Works on all screen sizes
- **Dark/Light Theme**: Automatic theme switching
- **Pull-to-Refresh**: Swipe down to refresh lists
- **Swipe Actions**: Quick access to edit/delete
- **Form Validation**: Real-time input validation
- **Error Handling**: User-friendly error messages
- **Loading States**: Progress indicators for all operations

## 🧪 Testing

The project includes:
- Unit test setup with JUnit
- UI test setup with Espresso
- Test dependencies configured in `build.gradle`

## 📦 Dependencies

Key dependencies used:
- **Compose BOM**: `2024.02.00`
- **Navigation**: `2.7.6`
- **Hilt**: `2.48`
- **Retrofit**: `2.9.0`
- **Coroutines**: `1.7.3`

## 🚨 Troubleshooting

### Common Issues
1. **Gradle Sync Fails**
   - Check internet connection
   - Clear Gradle cache: `./gradlew clean`
   - Update Android Studio

2. **App Crashes on Launch**
   - Check backend is running
   - Verify network permissions
   - Check logcat for error details

3. **Build Errors**
   - Ensure JDK 17 is installed
   - Check Android SDK version
   - Clean and rebuild project

## 📱 Screenshots

The app includes:
- **Students List**: Card-based layout with actions
- **Student Form**: Clean form with validation
- **Navigation**: Bottom navigation with 3 tabs
- **Error Handling**: Toast messages and error cards

## 🔮 Future Enhancements

- [ ] Offline support with Room database
- [ ] Push notifications
- [ ] Biometric authentication
- [ ] Dark/light theme toggle
- [ ] Multi-language support
- [ ] Advanced search and filtering
- [ ] Data export functionality
- [ ] User roles and permissions

## 📄 License

This project is part of the Polis University Management System.

## 🤝 Contributing

To extend the app:
1. Follow the existing code patterns
2. Use the same architecture (MVVM + Repository)
3. Maintain Material 3 design consistency
4. Add proper error handling
5. Include loading states
6. Test thoroughly before submitting

---

**Happy Coding! 🎉**
