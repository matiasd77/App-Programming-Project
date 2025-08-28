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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PolisUniversityTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PolisUniversityAppContent()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PolisUniversityAppContent() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                listOf(
                    NavigationItem(
                        route = "students",
                        title = "Students",
                        icon = Icons.Default.People
                    ),
                    NavigationItem(
                        route = "teachers",
                        title = "Teachers",
                        icon = Icons.Default.Person
                    ),
                    NavigationItem(
                        route = "courses",
                        title = "Courses",
                        icon = Icons.Default.School
                    )
                ).forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "students",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("students") {
                StudentListScreen(
                    onNavigateToAdd = {
                        navController.navigate("student_form")
                    },
                    onNavigateToEdit = { student ->
                        navController.navigate("student_form?studentId=${student.id}")
                    }
                )
            }
            
            composable("student_form") {
                StudentFormScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable("student_form?studentId={studentId}") { backStackEntry ->
                val studentId = backStackEntry.arguments?.getString("studentId")?.toIntOrNull()
                StudentFormScreen(
                    studentId = studentId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable("teacher_form") {
                TeacherFormScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable("course_form") {
                CourseFormScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            
            composable("teachers") {
                TeacherListScreen(
                    onNavigateToAdd = {
                        navController.navigate("teacher_form")
                    },
                    onNavigateToEdit = { teacher ->
                        navController.navigate("teacher_form?teacherId=${teacher.id}")
                    }
                )
            }
            
            composable("courses") {
                CourseListScreen(
                    onNavigateToAdd = {
                        navController.navigate("course_form")
                    },
                    onNavigateToEdit = { course ->
                        navController.navigate("course_form?courseId=${course.id}")
                    }
                )
            }
        }
    }
}

data class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)
