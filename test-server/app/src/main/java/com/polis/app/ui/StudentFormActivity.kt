package com.polis.app.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.polis.app.R
import com.polis.app.api.RetrofitClient
import com.polis.app.databinding.ActivityStudentFormBinding
import com.polis.app.model.*
import kotlinx.coroutines.launch

class StudentFormActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityStudentFormBinding
    private var studentId: Long? = null
    private var isEditMode = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupForm()
        loadStudentData()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        studentId = intent.getLongExtra("student_id", -1)
        isEditMode = studentId != null && studentId != -1L
        
        supportActionBar?.title = if (isEditMode) {
            getString(R.string.edit_student)
        } else {
            getString(R.string.add_student)
        }
    }
    
    private fun setupForm() {
        binding.btnSave.setOnClickListener {
            if (validateForm()) {
                saveStudent()
            }
        }
    }
    
    private fun loadStudentData() {
        if (!isEditMode) return
        
        studentId?.let { id ->
            lifecycleScope.launch {
                try {
                    val request = LongIdDto(id = id)
                    val response = RetrofitClient.studentService.getStudent(request)
                    
                    if (response.isSuccessful) {
                        val result = response.body()
                        result?.data?.let { student ->
                            populateForm(student)
                        }
                    } else {
                        showError("Failed to load student: ${response.code()}")
                    }
                } catch (e: Exception) {
                    showError("Network error: ${e.message}")
                }
            }
        }
    }
    
    private fun populateForm(student: StudentDto) {
        binding.apply {
            editTextFirstName.setText(student.firstName)
            editTextLastName.setText(student.lastName)
            editTextSerialNumber.setText(student.serialNumber)
        }
    }
    
    private fun validateForm(): Boolean {
        val firstName = binding.editTextFirstName.text.toString().trim()
        val lastName = binding.editTextLastName.text.toString().trim()
        val serialNumber = binding.editTextSerialNumber.text.toString().trim()
        
        if (firstName.isEmpty()) {
            binding.editTextFirstName.error = "First name is required"
            return false
        }
        
        if (lastName.isEmpty()) {
            binding.editTextLastName.error = "Last name is required"
            return false
        }
        
        if (serialNumber.isEmpty()) {
            binding.editTextSerialNumber.error = "Serial number is required"
            return false
        }
        
        return true
    }
    
    private fun saveStudent() {
        val firstName = binding.editTextFirstName.text.toString().trim()
        val lastName = binding.editTextLastName.text.toString().trim()
        val serialNumber = binding.editTextSerialNumber.text.toString().trim()
        
        val student = StudentDto(
            id = if (isEditMode) studentId else null,
            firstName = firstName,
            lastName = lastName,
            serialNumber = serialNumber
        )
        
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.studentService.upsertStudent(student)
                
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null) {
                        val message = if (isEditMode) {
                            getString(R.string.student_saved)
                        } else {
                            getString(R.string.student_saved)
                        }
                        Toast.makeText(this@StudentFormActivity, message, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = parseErrorMessage(errorBody)
                    showError(errorMessage)
                }
            } catch (e: Exception) {
                showError("Network error: ${e.message}")
            }
        }
    }
    
    private fun parseErrorMessage(errorBody: String?): String {
        return when {
            errorBody?.contains("VALIDATION_ERROR") == true -> getString(R.string.error_validation)
            errorBody?.contains("STUDENT_NOT_FOUND") == true -> getString(R.string.error_student_not_found)
            else -> getString(R.string.error_unknown)
        }
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
} 