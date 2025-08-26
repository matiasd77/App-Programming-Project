package com.polis.university.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polis.university.data.dto.StudentDto
import com.polis.university.data.repository.StudentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentFormViewModel @Inject constructor(
    private val studentRepository: StudentRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(StudentFormUiState())
    val uiState: StateFlow<StudentFormUiState> = _uiState.asStateFlow()
    
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
    
    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = if (!isValidEmail(email)) "Please enter a valid email" else null
        )
    }
    
    fun updatePhone(phone: String) {
        _uiState.value = _uiState.value.copy(phone = phone)
    }
    
    fun setStudentForEdit(student: StudentDto?) {
        student?.let {
            _uiState.value = _uiState.value.copy(
                firstName = it.firstName,
                lastName = it.lastName,
                email = it.email,
                phone = it.phone ?: "",
                isEditMode = true,
                studentId = it.id
            )
        }
    }
    
    fun saveStudent(onSuccess: () -> Unit) {
        if (!validateForm()) {
            return
        }
        
        _uiState.value = _uiState.value.copy(isSaving = true)
        
        val student = StudentDto(
            id = _uiState.value.studentId,
            firstName = _uiState.value.firstName,
            lastName = _uiState.value.lastName,
            email = _uiState.value.email,
            phone = _uiState.value.phone.takeIf { it.isNotBlank() }
        )
        
        viewModelScope.launch {
            val result = if (_uiState.value.isEditMode) {
                studentRepository.updateStudent(student)
            } else {
                studentRepository.createStudent(student)
            }
            
            result.collect { 
                it.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(isSaving = false)
                        onSuccess()
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            error = exception.message ?: "Failed to save student"
                        )
                    }
                )
            }
        }
    }
    
    private fun validateForm(): Boolean {
        val firstName = _uiState.value.firstName
        val lastName = _uiState.value.lastName
        val email = _uiState.value.email
        
        var isValid = true
        
        if (firstName.isBlank()) {
            _uiState.value = _uiState.value.copy(firstNameError = "First name is required")
            isValid = false
        }
        
        if (lastName.isBlank()) {
            _uiState.value = _uiState.value.copy(lastNameError = "Last name is required")
            isValid = false
        }
        
        if (!isValidEmail(email)) {
            _uiState.value = _uiState.value.copy(emailError = "Please enter a valid email")
            isValid = false
        }
        
        return isValid
    }
    
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class StudentFormUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val emailError: String? = null,
    val isEditMode: Boolean = false,
    val studentId: Int? = null,
    val isSaving: Boolean = false,
    val error: String? = null
)
