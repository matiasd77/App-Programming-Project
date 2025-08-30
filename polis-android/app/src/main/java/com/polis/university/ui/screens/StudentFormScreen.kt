// Student Form Screen for Polis University Android Application - Form for adding/editing student records

package com.polis.university.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.polis.university.data.dto.StudentDto
import com.polis.university.viewmodel.StudentFormViewModel

// Main student form screen composable for adding or editing student information
@OptIn(ExperimentalMaterial3Api::class) // Allow use of experimental Material3 components
@Composable
fun StudentFormScreen(
    student: StudentDto? = null, // Existing student data for editing (null for new students)
    studentId: Int? = null, // Student ID for loading existing data
    onNavigateBack: () -> Unit, // Callback to navigate back to previous screen
    viewModel: StudentFormViewModel = hiltViewModel() // Inject view model via Hilt
) {
    // Collect UI state from the view model
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Load existing student data when editing
    LaunchedEffect(student, studentId) {
        if (student != null) {
            viewModel.setStudentForEdit(student) // Set student data for editing
        } else if (studentId != null) {
            viewModel.loadStudentById(studentId) // Load student by ID
        }
    }
    
    // Clear any previous errors when screen is displayed
    LaunchedEffect(Unit) {
        viewModel.clearError() // Clear error state
    }
    
    // Create the main screen scaffold with top app bar
    Scaffold(
        topBar = {
            // Top app bar with dynamic title and back button
            TopAppBar(
                title = { 
                    Text(if (uiState.isEditMode) "Edit Student" else "Add Student") // Show appropriate title
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
        // Main form content in a scrollable column
        Column(
            modifier = Modifier
                .fillMaxSize() // Fill entire available space
                .padding(paddingValues) // Apply padding from scaffold
                .padding(16.dp) // Add additional padding
                .verticalScroll(rememberScrollState()), // Enable vertical scrolling
            horizontalAlignment = Alignment.CenterHorizontally // Center content horizontally
        ) {
            // First Name input field
            OutlinedTextField(
                value = uiState.firstName, // Current first name value
                onValueChange = { viewModel.updateFirstName(it) }, // Update first name in view model
                label = { Text("First Name *") }, // Field label with required indicator
                modifier = Modifier.fillMaxWidth(), // Make field fill full width
                isError = uiState.firstNameError != null, // Show error state if validation fails
                supportingText = {
                    // Display validation error message
                    uiState.firstNameError?.let { error ->
                        Text(error, color = MaterialTheme.colorScheme.error) // Show error in red
                    }
                },
                leadingIcon = {
                    // Person icon for first name field
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next // Move to next field on keyboard action
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp)) // Add spacing between fields
            
            // Last Name input field
            OutlinedTextField(
                value = uiState.lastName, // Current last name value
                onValueChange = { viewModel.updateLastName(it) }, // Update last name in view model
                label = { Text("Last Name *") }, // Field label with required indicator
                modifier = Modifier.fillMaxWidth(), // Make field fill full width
                isError = uiState.lastNameError != null, // Show error state if validation fails
                supportingText = {
                    // Display validation error message
                    uiState.lastNameError?.let { error ->
                        Text(error, color = MaterialTheme.colorScheme.error) // Show error in red
                    }
                },
                leadingIcon = {
                    // Person icon for last name field
                    Icon(Icons.Default.Person, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next // Move to next field on keyboard action
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp)) // Add spacing between fields
            
            // Email input field
            OutlinedTextField(
                value = uiState.email, // Current email value
                onValueChange = { viewModel.updateEmail(it) }, // Update email in view model
                label = { Text("Email *") }, // Field label with required indicator
                modifier = Modifier.fillMaxWidth(), // Make field fill full width
                isError = uiState.emailError != null, // Show error state if validation fails
                supportingText = {
                    // Display validation error message
                    uiState.emailError?.let { error ->
                        Text(error, color = MaterialTheme.colorScheme.error) // Show error in red
                    }
                },
                leadingIcon = {
                    // Email icon for email field
                    Icon(Icons.Default.Email, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email, // Use email keyboard type
                    imeAction = ImeAction.Next // Move to next field on keyboard action
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp)) // Add spacing between fields
            
            // Phone input field (optional)
            OutlinedTextField(
                value = uiState.phone, // Current phone value
                onValueChange = { viewModel.updatePhone(it) }, // Update phone in view model
                label = { Text("Phone (Optional)") }, // Field label indicating optional field
                modifier = Modifier.fillMaxWidth(), // Make field fill full width
                leadingIcon = {
                    // Phone icon for phone field
                    Icon(Icons.Default.Phone, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone, // Use phone keyboard type
                    imeAction = ImeAction.Done // Complete form on keyboard action
                )
            )
            
            Spacer(modifier = Modifier.height(32.dp)) // Add larger spacing before save button
            
            // Save button
            Button(
                onClick = { 
                    viewModel.saveStudent(onNavigateBack) // Save student and navigate back
                },
                modifier = Modifier.fillMaxWidth(), // Make button fill full width
                enabled = !uiState.isSaving // Disable button while saving
            ) {
                if (uiState.isSaving) {
                    // Show loading indicator while saving
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp), // Small loading indicator
                        color = MaterialTheme.colorScheme.onPrimary // Use onPrimary color
                    )
                    Spacer(modifier = Modifier.width(8.dp)) // Add spacing between indicator and text
                }
                // Show appropriate button text based on mode
                Text(if (uiState.isEditMode) "Update Student" else "Add Student")
            }
            
            // Error display section
            uiState.error?.let { error ->
                Spacer(modifier = Modifier.height(16.dp)) // Add spacing above error
                // Error card with error message
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer // Use error container color
                    )
                ) {
                    // Error content in a row layout
                    Row(
                        modifier = Modifier
                            .fillMaxWidth() // Fill full width
                            .padding(16.dp), // Add internal padding
                        verticalAlignment = Alignment.CenterVertically // Center content vertically
                    ) {
                        // Error icon
                        Icon(
                            Icons.Default.Error, // Error icon
                            contentDescription = null, // No content description needed
                            tint = MaterialTheme.colorScheme.error // Use error color
                        )
                        Spacer(modifier = Modifier.width(8.dp)) // Add spacing after icon
                        // Error message text
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer // Use onErrorContainer color
                        )
                    }
                }
            }
        }
    }
}
