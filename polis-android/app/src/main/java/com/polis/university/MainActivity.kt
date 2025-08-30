// Main Activity for Polis University Android Application - Handles navigation and main app structure

package com.polis.university

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.polis.university.ui.screens.*
import com.polis.university.ui.theme.PolisUniversityTheme
import dagger.hilt.android.AndroidEntryPoint

// Main Activity class that serves as the entry point for the user interface
@AndroidEntryPoint // Enables Hilt dependency injection for this activity
class MainActivity : ComponentActivity() {
    
    // Called when the activity is first created - sets up the main UI
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Call parent class onCreate method
        
        // Set the content using Jetpack Compose instead of traditional XML layouts
        setContent {
            // Apply the Polis University theme to all child composables
            PolisUniversityTheme {
                // Create a surface that fills the entire screen with background color
                Surface(
                    modifier = Modifier.fillMaxSize(), // Make surface fill entire available space
                    color = MaterialTheme.colorScheme.background // Use theme background color
                ) {
                    // Display the main app content with navigation
                    PolisUniversityAppContent()
                }
            }
        }
    }
}

// Main composable function that sets up the app's navigation structure and bottom navigation
@OptIn(ExperimentalMaterial3Api::class) // Allow use of experimental Material3 components
@Composable
fun PolisUniversityAppContent() {
    // Create a navigation controller to manage screen transitions
    val navController = rememberNavController()
    
    // Create the main app scaffold with bottom navigation bar
    Scaffold(
        bottomBar = {
            // Bottom navigation bar with tabs for different app sections
            NavigationBar {
                // Get current navigation state to highlight active tab
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                // Define the navigation items for the bottom bar
                listOf(
                    NavigationItem(
                        route = "home", // Navigation route identifier
                        title = "Home", // Display text for the tab
                        icon = Icons.Default.Home // Icon to display for the tab
                    ),
                    NavigationItem(
                        route = "students", // Route to students section
                        title = "Students", // Display text for students tab
                        icon = Icons.Default.People // People icon for students
                    ),
                    NavigationItem(
                        route = "teachers", // Route to teachers section
                        title = "Teachers", // Display text for teachers tab
                        icon = Icons.Default.Person // Person icon for teachers
                    ),
                    NavigationItem(
                        route = "courses", // Route to courses section
                        title = "Courses", // Display text for courses tab
                        icon = Icons.Default.School // School icon for courses
                    )
                ).forEach { item ->
                    // Create each navigation bar item
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) }, // Display the icon
                        label = { Text(item.title) }, // Display the tab title
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true, // Highlight if this tab is active
                        onClick = {
                            // Navigate to the selected tab when clicked
                            navController.navigate(item.route) {
                                // Pop up to the start destination (home) to avoid building up a large stack
                                popUpTo("home") {
                                    saveState = true // Save the current state
                                }
                                // Avoid multiple copies of the same destination
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        // Navigation host that manages all the different screens
        NavHost(
            navController = navController, // Use the navigation controller we created
            startDestination = "home", // Start with the home screen
            modifier = Modifier.padding(paddingValues) // Apply padding from the scaffold
        ) {
            // Define the home screen route
            composable("home") {
                HomeScreen(
                    onNavigateToStudents = {
                        // Navigate to students tab using the same logic as bottom navigation
                        navController.navigate("students") {
                            popUpTo("home") {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToTeachers = {
                        // Navigate to teachers tab using the same logic as bottom navigation
                        navController.navigate("teachers") {
                            popUpTo("home") {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToCourses = {
                        // Navigate to courses tab using the same logic as bottom navigation
                        navController.navigate("courses") {
                            popUpTo("home") {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            
            // Define the students list screen route
            composable("students") {
                StudentListScreen(
                    onNavigateToAdd = {
                        navController.navigate("student_form") // Navigate to add student form
                    },
                    onNavigateToEdit = { student ->
                        navController.navigate("student_form?studentId=${student.id}") // Navigate to edit form with student ID
                    }
                )
            }
            
            // Define the add student form route
            composable("student_form") {
                StudentFormScreen(
                    onNavigateBack = {
                        navController.popBackStack() // Go back to previous screen
                    }
                )
            }

            // Define the edit student form route with student ID parameter
            composable("student_form?studentId={studentId}") { backStackEntry ->
                val studentId = backStackEntry.arguments?.getString("studentId")?.toIntOrNull() // Extract student ID from route
                StudentFormScreen(
                    studentId = studentId, // Pass the student ID for editing
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Define the add teacher form route
            composable("teacher_form") {
                TeacherFormScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            // Define the edit teacher form route with teacher ID parameter
            composable("teacher_form?teacherId={teacherId}") { backStackEntry ->
                val teacherId = backStackEntry.arguments?.getString("teacherId")?.toIntOrNull() // Extract teacher ID from route
                TeacherFormScreen(
                    teacherId = teacherId, // Pass the teacher ID for editing
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Define the add course form route
            composable("course_form") {
                CourseFormScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            // Define the edit course form route with course ID parameter
            composable("course_form?courseId={courseId}") { backStackEntry ->
                val courseId = backStackEntry.arguments?.getString("courseId")?.toIntOrNull() // Extract course ID from route
                CourseFormScreen(
                    courseId = courseId, // Pass the course ID for editing
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            // Define the teachers list screen route
            composable("teachers") {
                TeacherListScreen(
                    onNavigateToAdd = {
                        navController.navigate("teacher_form") // Navigate to add teacher form
                    },
                    onNavigateToEdit = { teacher ->
                        navController.navigate("teacher_form?teacherId=${teacher.id}") // Navigate to edit form with teacher ID
                    }
                )
            }
            
            // Define the courses list screen route
            composable("courses") {
                CourseListScreen(
                    onNavigateToAdd = {
                        navController.navigate("course_form") // Navigate to add course form
                    },
                    onNavigateToEdit = { course ->
                        navController.navigate("course_form?courseId=${course.id}") // Navigate to edit form with course ID
                    }
                )
            }
        }
    }
}

// Data class representing a navigation item in the bottom navigation bar
data class NavigationItem(
    val route: String, // Navigation route identifier
    val title: String, // Display text for the navigation item
    val icon: ImageVector // Icon to display for the navigation item
)
