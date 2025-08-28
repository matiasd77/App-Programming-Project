package com.polis.university.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polis.university.data.dto.StudentDto
import com.polis.university.data.dto.StudentFilter
import com.polis.university.data.dto.Pagination
import com.polis.university.data.repository.StudentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentListViewModel @Inject constructor(
    private val studentRepository: StudentRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(StudentListUiState())
    val uiState: StateFlow<StudentListUiState> = _uiState.asStateFlow()
    
    init {
        loadStudents()
    }
    
    fun loadStudents(refresh: Boolean = false) {
        if (refresh) {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
        } else {
            _uiState.value = _uiState.value.copy(isLoading = true)
        }
        
        viewModelScope.launch {
            studentRepository.getStudents().collect { result ->
                result.fold(
                    onSuccess = { response ->
                        _uiState.value = _uiState.value.copy(
                            students = response.slice.content,
                            isLoading = false,
                            isRefreshing = false,
                            error = null,
                            hasNextPage = response.slice.hasNext
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = exception.message ?: "Unknown error occurred"
                        )
                    }
                )
            }
        }
    }
    
    fun deleteStudent(student: StudentDto) {
        viewModelScope.launch {
            studentRepository.deleteStudent(student.id!!).collect { result ->
                result.fold(
                    onSuccess = {
                        // Remove from local list
                        val currentStudents = _uiState.value.students.toMutableList()
                        currentStudents.remove(student)
                        _uiState.value = _uiState.value.copy(students = currentStudents)
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            error = "Failed to delete student: ${exception.message}"
                        )
                    }
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun searchStudents(query: String) {
        val filter = StudentFilter(
            filter = query.takeIf { it.isNotBlank() },
            pagination = Pagination(0, 200)
        )
        
        viewModelScope.launch {
            studentRepository.getStudents(filter).collect { result ->
                result.fold(
                    onSuccess = { response ->
                        _uiState.value = _uiState.value.copy(
                            students = response.slice.content,
                            isLoading = false,
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Search failed"
                        )
                    }
                )
            }
        }
    }
}

data class StudentListUiState(
    val students: List<StudentDto> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val hasNextPage: Boolean = false
)
