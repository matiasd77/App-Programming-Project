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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherFormScreen(
    onNavigateBack: () -> Unit
) {
    val viewModel: TeacherFormViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.teacherId != null) "Edit Teacher" else "Add Teacher") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.firstName,
                onValueChange = { viewModel.updateFirstName(it) },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.firstNameError != null,
                supportingText = {
                    if (uiState.firstNameError != null) {
                        Text(uiState.firstNameError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )
            
            OutlinedTextField(
                value = uiState.lastName,
                onValueChange = { viewModel.updateLastName(it) },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.lastNameError != null,
                supportingText = {
                    if (uiState.lastNameError != null) {
                        Text(uiState.lastNameError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )
            
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.updateTitle(it) },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.titleError != null,
                supportingText = {
                    if (uiState.titleError != null) {
                        Text(uiState.titleError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { viewModel.saveTeacher() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (uiState.isSaving) "Saving..." else "Save Teacher")
            }
            
            if (uiState.error != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.error!!,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.clearError() }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Dismiss",
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }
    }
}
