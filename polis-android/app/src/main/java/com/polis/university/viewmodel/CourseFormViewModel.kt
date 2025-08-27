package com.polis.university.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polis.university.data.dto.CourseDto
import com.polis.university.data.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseFormViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CourseFormUiState())
    val uiState: StateFlow<CourseFormUiState> = _uiState.asStateFlow()
    
    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(
            title = title,
            titleError = if (title.isBlank()) "Title is required" else null
        )
    }
    
    fun updateCode(code: String) {
        _uiState.value = _uiState.value.copy(
            code = code,
            codeError = if (code.isBlank()) "Code is required" else null
        )
    }
    
    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }
    
    fun updateYear(year: String) {
        val yearInt = year.toIntOrNull()
        _uiState.value = _uiState.value.copy(
            year = year,
            yearError = if (yearInt == null || yearInt <= 0) "Valid year is required" else null
        )
    }
    
    fun saveCourse() {
        val currentState = _uiState.value
        
        // Validate inputs
        if (currentState.title.isBlank() || currentState.code.isBlank() || 
            currentState.year.toIntOrNull() == null || currentState.year.toIntOrNull()!! <= 0) {
            _uiState.value = currentState.copy(
                titleError = if (currentState.title.isBlank()) "Title is required" else null,
                codeError = if (currentState.code.isBlank()) "Code is required" else null,
                yearError = if (currentState.year.toIntOrNull() == null || currentState.year.toIntOrNull()!! <= 0) 
                    "Valid year is required" else null
            )
            return
        }
        
        val course = CourseDto(
            id = currentState.courseId,
            title = currentState.title,
            code = currentState.code,
            description = currentState.description.ifBlank { null },
            year = currentState.year.toIntOrNull()
        )
        
        viewModelScope.launch {
            _uiState.value = currentState.copy(isSaving = true, error = null)
            
            val result = if (currentState.courseId != null) {
                courseRepository.updateCourse(course)
            } else {
                courseRepository.createCourse(course)
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
                            error = exception.message ?: "Failed to save course"
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
    
    fun loadCourse(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            courseRepository.getCourse(id).collect { result ->
                result.fold(
                    onSuccess = { course ->
                        _uiState.value = _uiState.value.copy(
                            courseId = course.id,
                            title = course.title,
                            code = course.code,
                            description = course.description ?: "",
                            year = course.year?.toString() ?: "",
                            isLoading = false
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            error = exception.message ?: "Failed to load course",
                            isLoading = false
                        )
                    }
                )
            }
        }
    }
}

data class CourseFormUiState(
    val courseId: Int? = null,
    val title: String = "",
    val code: String = "",
    val description: String = "",
    val year: String = "",
    val titleError: String? = null,
    val codeError: String? = null,
    val yearError: String? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
