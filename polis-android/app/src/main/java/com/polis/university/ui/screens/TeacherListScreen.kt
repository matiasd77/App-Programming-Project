// Teacher List Screen for Polis University Android Application - Displays and manages teacher records

package com.polis.university.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.polis.university.data.dto.TeacherDto
import com.polis.university.viewmodel.TeacherListViewModel

// Main teacher list screen composable that displays all registered teachers
@OptIn(ExperimentalMaterial3Api::class) // Allow use of experimental Material3 components
@Composable
fun TeacherListScreen(
    onNavigateToAdd: () -> Unit = {}, // Callback to navigate to add teacher form (defaults to empty)
    onNavigateToEdit: (TeacherDto) -> Unit = {} // Callback to navigate to edit teacher form (defaults to empty)
) {
    // Inject view model via Hilt
    val viewModel: TeacherListViewModel = hiltViewModel()
    // Collect UI state from the view model
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Get current lifecycle owner for automatic refresh
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // Refresh list when returning to this screen (e.g., after adding/editing)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadTeachers() // Load teachers when screen resumes
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer) // Add lifecycle observer
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) } // Remove observer on dispose
    }
    
    // State to control delete confirmation dialog
    var showDeleteDialog by remember { mutableStateOf<TeacherDto?>(null) }
    
    // Create the main screen scaffold with top app bar and floating action button
    Scaffold(
        topBar = {
            // Top app bar with title and refresh button
            TopAppBar(
                title = { Text("Teachers") }, // Display screen title
                actions = {
                    // Refresh button to reload teacher data
                    IconButton(onClick = { viewModel.refreshTeachers() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh") // Refresh icon
                    }
                }
            )
        },
        floatingActionButton = {
            // Floating action button to add new teachers
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add Teacher") // Add icon
            }
        }
    ) { paddingValues ->
        // Main content box that handles different UI states
        Box(
            modifier = Modifier
                .fillMaxSize() // Fill entire available space
                .padding(paddingValues) // Apply padding from scaffold
        ) {
            // Handle different UI states based on data loading and availability
            when {
                uiState.isLoading -> {
                    // Show loading indicator when data is being fetched
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center) // Center the loading indicator
                    )
                }
                uiState.error != null -> {
                    // Show error state when an error occurs
                    ErrorState(
                        error = uiState.error!!, // Pass error message
                        onRetry = { viewModel.loadTeachers() }, // Pass retry callback
                        onDismiss = { viewModel.clearError() } // Pass dismiss callback
                    )
                }
                uiState.teachers.isEmpty() -> {
                    // Show empty state when no teachers are found
                    EmptyState(
                        message = "No teachers found", // Empty state message
                        onAddClick = onNavigateToAdd // Pass add callback
                    )
                }
                else -> {
                    // Show teacher list when data is available
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(), // Fill entire available space
                        contentPadding = PaddingValues(16.dp), // Add padding around content
                        verticalArrangement = Arrangement.spacedBy(8.dp) // Space items apart
                    ) {
                        // Display each teacher as a list item
                        items(uiState.teachers) { teacher ->
                            // Create teacher card for each teacher
                            TeacherCard(
                                teacher = teacher, // Pass teacher data
                                onEdit = { onNavigateToEdit(teacher) }, // Pass edit callback
                                onDelete = { showDeleteDialog = teacher } // Show delete dialog
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Delete confirmation dialog
    showDeleteDialog?.let { teacher ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null }, // Close dialog when dismissed
            title = { Text("Delete Teacher") }, // Dialog title
            text = { Text("Are you sure you want to delete ${teacher.firstName} ${teacher.lastName}?") }, // Confirmation message
            confirmButton = {
                // Delete confirmation button
                TextButton(
                    onClick = {
                        viewModel.deleteTeacher(teacher) // Execute delete operation
                        showDeleteDialog = null // Close dialog
                    }
                ) {
                    Text("Delete") // Button text
                }
            },
            dismissButton = {
                // Cancel button
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel") // Button text
                }
            }
        )
    }
}

// Composable function that renders an individual teacher card
@OptIn(ExperimentalMaterial3Api::class) // Allow use of experimental Material3 components
@Composable
fun TeacherCard(
    teacher: TeacherDto, // Teacher data to display
    onEdit: () -> Unit, // Callback for edit button
    onDelete: () -> Unit // Callback for delete button
) {
    // Create clickable card to display teacher information
    Card(
        modifier = Modifier.fillMaxWidth(), // Make card fill full width
        onClick = onEdit // Handle click events for editing
    ) {
        // Card content in a row layout
        Row(
            modifier = Modifier
                .fillMaxWidth() // Fill full width
                .padding(16.dp), // Add internal padding
            horizontalArrangement = Arrangement.SpaceBetween, // Space content apart
            verticalAlignment = Alignment.CenterVertically // Center content vertically
        ) {
            // Left side: Teacher information
            Column(modifier = Modifier.weight(1f)) { // Take remaining space
                // Display teacher's full name
                Text(
                    text = "${teacher.firstName} ${teacher.lastName}", // Combine first and last name
                    style = MaterialTheme.typography.titleMedium // Use title medium style
                )
                // Display teacher's title
                Text(
                    text = teacher.title,
                    style = MaterialTheme.typography.bodyMedium, // Use body medium style
                    color = MaterialTheme.colorScheme.onSurfaceVariant // Use variant color
                )
            }
            // Right side: Action buttons
            Row {
                // Edit button
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit, // Edit icon
                        contentDescription = "Edit", // Accessibility description
                        tint = MaterialTheme.colorScheme.primary // Use primary color
                    )
                }
                // Delete button
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete, // Delete icon
                        contentDescription = "Delete", // Accessibility description
                        tint = MaterialTheme.colorScheme.error // Use error color
                    )
                }
            }
        }
    }
}

// Composable function that displays an empty state when no teachers are found
@Composable
private fun EmptyState(
    message: String, // Message to display in empty state
    onAddClick: () -> Unit // Callback for add button
) {
    // Center content in a column layout
    Column(
        modifier = Modifier.fillMaxSize(), // Fill entire available space
        horizontalAlignment = Alignment.CenterHorizontally, // Center content horizontally
        verticalArrangement = Arrangement.Center // Center content vertically
    ) {
        // Display person icon
        Icon(
            Icons.Default.Person, // Person icon
            contentDescription = null, // No content description needed
            modifier = Modifier.size(64.dp), // Make icon large
            tint = MaterialTheme.colorScheme.onSurfaceVariant // Use variant color
        )
        Spacer(modifier = Modifier.height(16.dp)) // Add spacing below icon
        
        // Display empty state message
        Text(
            text = message, // Main message
            fontSize = 18.sp, // Set font size
            color = MaterialTheme.colorScheme.onSurfaceVariant, // Use variant color
            textAlign = TextAlign.Center // Center align text
        )
        Spacer(modifier = Modifier.height(16.dp)) // Add spacing below message
        
        // Add teacher button
        Button(onClick = onAddClick) {
            Icon(Icons.Default.Add, contentDescription = null) // Add icon
            Spacer(modifier = Modifier.width(8.dp)) // Add spacing between icon and text
            Text("Add Teacher") // Button text
        }
    }
}

// Composable function that displays an error state when operations fail
@Composable
private fun ErrorState(
    error: String, // Error message to display
    onRetry: () -> Unit, // Callback for retry button
    onDismiss: () -> Unit // Callback for dismiss button
) {
    // Center content in a column layout
    Column(
        modifier = Modifier.fillMaxSize(), // Fill entire available space
        horizontalAlignment = Alignment.CenterHorizontally, // Center content horizontally
        verticalArrangement = Arrangement.Center // Center content vertically
    ) {
        // Display error icon
        Icon(
            Icons.Default.Error, // Error icon
            contentDescription = null, // No content description needed
            modifier = Modifier.size(64.dp), // Make icon large
            tint = MaterialTheme.colorScheme.error // Use error color
        )
        Spacer(modifier = Modifier.height(16.dp)) // Add spacing below icon
        
        // Display error message
        Text(
            text = error, // Error message
            fontSize = 16.sp, // Set font size
            color = MaterialTheme.colorScheme.error, // Use error color
            textAlign = TextAlign.Center // Center align text
        )
        Spacer(modifier = Modifier.height(16.dp)) // Add spacing below message
        
        // Action buttons row
        Row {
            // Retry button
            Button(onClick = onRetry) {
                Text("Retry") // Button text
            }
            Spacer(modifier = Modifier.width(8.dp)) // Add spacing between buttons
            // Dismiss button
            Button(onClick = onDismiss) {
                Text("Dismiss") // Button text
            }
        }
    }
}
