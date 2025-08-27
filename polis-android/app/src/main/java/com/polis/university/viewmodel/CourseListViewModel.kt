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
class CourseListViewModel @Inject constructor(
    private val courseRepository: CourseRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CourseListUiState())
    val uiState: StateFlow<CourseListUiState> = _uiState.asStateFlow()
    
    init {
        loadCourses()
    }
    
    fun loadCourses() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            courseRepository.getCourses().collect { result ->
                result.fold(
                    onSuccess = { response ->
                        _uiState.value = _uiState.value.copy(
                            courses = response.slice.content,
                            isLoading = false,
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Unknown error occurred"
                        )
                    }
                )
            }
        }
    }
    
    fun refreshCourses() {
        loadCourses()
    }
    
    fun deleteCourse(course: CourseDto) {
        viewModelScope.launch {
            courseRepository.deleteCourse(course.id!!).collect { result ->
                result.fold(
                    onSuccess = {
                        loadCourses() // Reload the list
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            error = "Failed to delete course: ${exception.message}"
                        )
                    }
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class CourseListUiState(
    val courses: List<CourseDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
