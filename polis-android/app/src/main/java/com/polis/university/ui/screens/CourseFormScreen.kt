package com.polis.university.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.polis.university.viewmodel.CourseFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseFormScreen(
    onNavigateBack: () -> Unit
) {
    val viewModel: CourseFormViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.courseId != null) "Edit Course" else "Add Course") },
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
            
            OutlinedTextField(
                value = uiState.code,
                onValueChange = { viewModel.updateCode(it) },
                label = { Text("Code") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.codeError != null,
                supportingText = {
                    if (uiState.codeError != null) {
                        Text(uiState.codeError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )
            
            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            
            OutlinedTextField(
                value = uiState.year,
                onValueChange = { viewModel.updateYear(it) },
                label = { Text("Year") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.yearError != null,
                supportingText = {
                    if (uiState.yearError != null) {
                        Text(uiState.yearError!!, color = MaterialTheme.colorScheme.error)
                    }
                }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { viewModel.saveCourse() },
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
                Text(if (uiState.isSaving) "Saving..." else "Save Course")
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
