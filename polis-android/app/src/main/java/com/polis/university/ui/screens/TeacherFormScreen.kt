// Teacher Form Screen for Polis University Android Application - Form for adding/editing teacher records

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
import com.polis.university.viewmodel.TeacherFormViewModel

// Main teacher form screen composable for adding or editing teacher information
@OptIn(ExperimentalMaterial3Api::class) // Allow use of experimental Material3 components
@Composable
fun TeacherFormScreen(
    teacherId: Int? = null, // Teacher ID for loading existing data in edit mode
    onNavigateBack: () -> Unit // Callback to navigate back to previous screen
) {
    // Inject view model via Hilt
    val viewModel: TeacherFormViewModel = hiltViewModel()
    // Collect UI state from the view model
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Load existing teacher data when editing
    LaunchedEffect(teacherId) {
        if (teacherId != null) {
            viewModel.loadTeacherById(teacherId) // Load teacher by ID
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
                    Text(if (uiState.teacherId != null) "Edit Teacher" else "Add Teacher") // Show appropriate title
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
            // First Name input field
            OutlinedTextField(
                value = uiState.firstName, // Current first name value
                onValueChange = { viewModel.updateFirstName(it) }, // Update first name in view model
                label = { Text("First Name") }, // Field label
                modifier = Modifier.fillMaxWidth(), // Make field fill full width
                isError = uiState.firstNameError != null, // Show error state if validation fails
                supportingText = {
                    // Display validation error message
                    if (uiState.firstNameError != null) {
                        Text(uiState.firstNameError!!, color = MaterialTheme.colorScheme.error) // Show error in red
                    }
                }
            )
            
            // Last Name input field
            OutlinedTextField(
                value = uiState.lastName, // Current last name value
                onValueChange = { viewModel.updateLastName(it) }, // Update last name in view model
                label = { Text("Last Name") }, // Field label
                modifier = Modifier.fillMaxWidth(), // Make field fill full width
                isError = uiState.lastNameError != null, // Show error state if validation fails
                supportingText = {
                    // Display validation error message
                    if (uiState.lastNameError != null) {
                        Text(uiState.lastNameError!!, color = MaterialTheme.colorScheme.error) // Show error in red
                    }
                }
            )
            
            // Title input field
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
            
            // Flexible spacer to push save button to bottom
            Spacer(modifier = Modifier.weight(1f)) // Take remaining space
            
            // Save button
            Button(
                onClick = { viewModel.saveTeacher() }, // Save teacher data
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
                Text(if (uiState.isSaving) "Saving..." else if (uiState.teacherId != null) "Update Teacher" else "Save Teacher")
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
