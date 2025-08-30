// Course Form Screen for Polis University Android Application - Form for adding/editing course records

package com.polis.university.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.polis.university.viewmodel.CourseFormViewModel

// Main course form screen composable for adding or editing course information
@OptIn(ExperimentalMaterial3Api::class) // Allow use of experimental Material3 components
@Composable
fun CourseFormScreen(
    courseId: Int? = null, // Course ID for loading existing data in edit mode
    onNavigateBack: () -> Unit // Callback to navigate back to previous screen
) {
    // Inject view model via Hilt
    val viewModel: CourseFormViewModel = hiltViewModel()
    // Collect UI state from the view model
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Load existing course data when editing
    LaunchedEffect(courseId) {
        if (courseId != null) {
            viewModel.loadCourse(courseId) // Load course by ID
        }
    }
    
    // Automatically navigate back on successful save
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onNavigateBack() // Navigate back when save is successful
        }
    }
    
    // Create the main screen scaffold with top app bar
    Scaffold(
        topBar = {
            // Top app bar with dynamic title and back button
            TopAppBar(
                title = { 
                    Text(if (uiState.courseId != null) "Edit Course" else "Add Course") // Show appropriate title
                },
                navigationIcon = {
                    // Back button to return to previous screen
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back") // Back arrow icon
                    }
                }
            )
        }
    ) { paddingValues ->
        // Main form content in a column layout
        Column(
            modifier = Modifier
                .fillMaxSize() // Fill entire available space
                .padding(paddingValues) // Apply padding from scaffold
                .padding(16.dp), // Add additional padding
            verticalArrangement = Arrangement.spacedBy(16.dp) // Space form fields apart
        ) {
            // Course Title input field
            OutlinedTextField(
                value = uiState.title, // Current title value
                onValueChange = { viewModel.updateTitle(it) }, // Update title in view model
                label = { Text("Title") }, // Field label
                modifier = Modifier.fillMaxWidth(), // Make field fill full width
                isError = uiState.titleError != null, // Show error state if validation fails
                supportingText = {
                    // Display validation error message
                    if (uiState.titleError != null) {
                        Text(uiState.titleError!!, color = MaterialTheme.colorScheme.error) // Show error in red
                    }
                }
            )
            
            // Course Code input field
            OutlinedTextField(
                value = uiState.code, // Current code value
                onValueChange = { viewModel.updateCode(it) }, // Update code in view model
                label = { Text("Code") }, // Field label
                modifier = Modifier.fillMaxWidth(), // Make field fill full width
                isError = uiState.codeError != null, // Show error state if validation fails
                supportingText = {
                    // Display validation error message
                    if (uiState.codeError != null) {
                        Text(uiState.codeError!!, color = MaterialTheme.colorScheme.error) // Show error in red
                    }
                }
            )
            
            // Course Description input field (optional)
            OutlinedTextField(
                value = uiState.description, // Current description value
                onValueChange = { viewModel.updateDescription(it) }, // Update description in view model
                label = { Text("Description (Optional)") }, // Field label indicating optional field
                modifier = Modifier.fillMaxWidth(), // Make field fill full width
                minLines = 3 // Allow multiple lines for description
            )
            
            // Course Year input field
            OutlinedTextField(
                value = uiState.year, // Current year value
                onValueChange = { viewModel.updateYear(it) }, // Update year in view model
                label = { Text("Year") }, // Field label
                modifier = Modifier.fillMaxWidth(), // Make field fill full width
                isError = uiState.yearError != null, // Show error state if validation fails
                supportingText = {
                    // Display validation error message
                    if (uiState.yearError != null) {
                        Text(uiState.yearError!!, color = MaterialTheme.colorScheme.error) // Show error in red
                    }
                }
            )
            
            // Flexible spacer to push save button to bottom
            Spacer(modifier = Modifier.weight(1f)) // Take remaining space
            
            // Save button
            Button(
                onClick = { viewModel.saveCourse() }, // Save course data
                modifier = Modifier.fillMaxWidth(), // Make button fill full width
                enabled = !uiState.isSaving // Disable button while saving
            ) {
                if (uiState.isSaving) {
                    // Show loading indicator while saving
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp), // Small loading indicator
                        color = MaterialTheme.colorScheme.onPrimary // Use onPrimary color
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Add spacing between indicator and text
                }
                // Show appropriate button text based on state and mode
                Text(if (uiState.isSaving) "Saving..." else if (uiState.courseId != null) "Update Course" else "Save Course")
            }
            
            // Error display section
            if (uiState.error != null) {
                // Error card with error message and dismiss button
                Card(
                    modifier = Modifier.fillMaxWidth(), // Make card fill full width
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer // Use error container color
                    )
                ) {
                    // Error content in a row layout
                    Row(
                        modifier = Modifier
                            .fillMaxWidth() // Fill full width
                            .padding(16.dp), // Add internal padding
                        horizontalArrangement = Arrangement.SpaceBetween, // Space content apart
                        verticalAlignment = Alignment.CenterVertically // Center content vertically
                    ) {
                        // Error message text
                        Text(
                            text = uiState.error!!, // Display error message
                            color = MaterialTheme.colorScheme.onErrorContainer, // Use onErrorContainer color
                            modifier = Modifier.weight(1f) // Take remaining space
                        )
                        // Dismiss button
                        IconButton(onClick = { viewModel.clearError() }) {
                            Icon(
                                Icons.Default.Close, // Close icon
                                contentDescription = "Dismiss", // Accessibility description
                                tint = MaterialTheme.colorScheme.onErrorContainer // Use onErrorContainer color
                            )
                        }
                    }
                }
            }
        }
    }
}
