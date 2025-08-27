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
class TeacherListViewModel @Inject constructor(
    private val teacherRepository: TeacherRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TeacherListUiState())
    val uiState: StateFlow<TeacherListUiState> = _uiState.asStateFlow()
    
    init {
        loadTeachers()
    }
    
    fun loadTeachers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            teacherRepository.getTeachers().collect { result ->
                result.fold(
                    onSuccess = { response ->
                        _uiState.value = _uiState.value.copy(
                            teachers = response.slice.content,
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
    
    fun refreshTeachers() {
        loadTeachers()
    }
    
    fun deleteTeacher(teacher: TeacherDto) {
        viewModelScope.launch {
            teacherRepository.deleteTeacher(teacher.id!!).collect { result ->
                result.fold(
                    onSuccess = {
                        loadTeachers() // Reload the list
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            error = "Failed to delete teacher: ${exception.message}"
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

data class TeacherListUiState(
    val teachers: List<TeacherDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
