package com.polis.university.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polis.university.data.dto.TeacherDto
import com.polis.university.data.repository.TeacherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherFormViewModel @Inject constructor(
    private val teacherRepository: TeacherRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TeacherFormUiState())
    val uiState: StateFlow<TeacherFormUiState> = _uiState.asStateFlow()
    
    fun updateFirstName(firstName: String) {
        _uiState.value = _uiState.value.copy(
            firstName = firstName,
            firstNameError = if (firstName.isBlank()) "First name is required" else null
        )
    }
    
    fun updateLastName(lastName: String) {
        _uiState.value = _uiState.value.copy(
            lastName = lastName,
            lastNameError = if (lastName.isBlank()) "Last name is required" else null
        )
    }
    
    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(
            title = title,
            titleError = if (title.isBlank()) "Title is required" else null
        )
    }
    
    fun saveTeacher() {
        val currentState = _uiState.value
        
        // Validate inputs
        if (currentState.firstName.isBlank() || currentState.lastName.isBlank() || currentState.title.isBlank()) {
            _uiState.value = currentState.copy(
                firstNameError = if (currentState.firstName.isBlank()) "First name is required" else null,
                lastNameError = if (currentState.lastName.isBlank()) "Last name is required" else null,
                titleError = if (currentState.title.isBlank()) "Title is required" else null
            )
            return
        }
        
        val teacher = TeacherDto(
            id = currentState.teacherId,
            firstName = currentState.firstName,
            lastName = currentState.lastName,
            title = currentState.title
        )
        
        viewModelScope.launch {
            _uiState.value = currentState.copy(isSaving = true, error = null)
            
            val result = if (currentState.teacherId != null) {
                teacherRepository.updateTeacher(teacher)
            } else {
                teacherRepository.createTeacher(teacher)
            }
            
            result.collect { result ->
                result.fold(
                    onSuccess = {
                        _uiState.value = currentState.copy(
                            isSaving = false,
                            isSuccess = true
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = currentState.copy(
                            isSaving = false,
                            error = exception.message ?: "Failed to save teacher"
                        )
                    }
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }
    
    fun loadTeacherById(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            teacherRepository.getTeacher(id).collect { result ->
                result.fold(
                    onSuccess = { teacher ->
                        _uiState.value = _uiState.value.copy(
                            teacherId = teacher.id,
                            firstName = teacher.firstName,
                            lastName = teacher.lastName,
                            title = teacher.title,
                            isLoading = false
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            error = exception.message ?: "Failed to load teacher",
                            isLoading = false
                        )
                    }
                )
            }
        }
    }
}

data class TeacherFormUiState(
    val teacherId: Int? = null,
    val firstName: String = "",
    val lastName: String = "",
    val title: String = "",
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val titleError: String? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
