package com.polis.app.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputLayout
import com.polis.app.R
import com.polis.app.api.RetrofitClient
import com.polis.app.databinding.ActivityTeacherFormBinding
import com.polis.app.model.LongIdDto
import com.polis.app.model.TeacherDto
import kotlinx.coroutines.launch

class TeacherFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherFormBinding
    private var teacherId: Long? = null
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupForm()
        loadTeacherData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = if (isEditMode) getString(R.string.edit_teacher) else getString(R.string.add_teacher)
    }

    private fun setupForm() {
        binding.saveButton.setOnClickListener {
            if (validateForm()) {
                saveTeacher()
            }
        }
    }

    private fun loadTeacherData() {
        teacherId = intent.getLongExtra("teacher_id", -1)
        isEditMode = teacherId != -1L

        if (isEditMode) {
            loadExistingTeacher()
        } else {
            supportActionBar?.title = getString(R.string.add_teacher)
        }
    }

    private fun loadExistingTeacher() {
        teacherId?.let { id ->
            lifecycleScope.launch {
                try {
                    val request = LongIdDto(id = id)
                    val response = RetrofitClient.teacherService.getTeacher(request)

                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result.data != null) {
                            val teacher = result.data
                            populateForm(teacher)
                            supportActionBar?.title = getString(R.string.edit_teacher)
                        } else {
                            showError("Teacher not found")
                            finish()
                        }
                    } else {
                        showError("Failed to load teacher: ${response.code()}")
                        finish()
                    }
                } catch (e: Exception) {
                    showError("Network error: ${e.message}")
                    finish()
                }
            }
        }
    }

    private fun populateForm(teacher: TeacherDto) {
        binding.firstNameInput.editText?.setText(teacher.firstName)
        binding.lastNameInput.editText?.setText(teacher.lastName)
        binding.titleInput.editText?.setText(teacher.title)
    }

    private fun validateForm(): Boolean {
        var isValid = true

        // Validate first name
        val firstName = binding.firstNameInput.editText?.text?.toString()?.trim()
        if (firstName.isNullOrEmpty()) {
            binding.firstNameInput.error = getString(R.string.error_first_name_required)
            isValid = false
        } else {
            binding.firstNameInput.error = null
        }

        // Validate last name
        val lastName = binding.lastNameInput.editText?.text?.toString()?.trim()
        if (lastName.isNullOrEmpty()) {
            binding.lastNameInput.error = getString(R.string.error_last_name_required)
            isValid = false
        } else {
            binding.lastNameInput.error = null
        }

        // Validate title
        val title = binding.titleInput.editText?.text?.toString()?.trim()
        if (title.isNullOrEmpty()) {
            binding.titleInput.error = getString(R.string.error_title_required)
            isValid = false
        } else {
            binding.titleInput.error = null
        }

        return isValid
    }

    private fun saveTeacher() {
        val firstName = binding.firstNameInput.editText?.text?.toString()?.trim() ?: ""
        val lastName = binding.lastNameInput.editText?.text?.toString()?.trim() ?: ""
        val title = binding.titleInput.editText?.text?.toString()?.trim() ?: ""

        val teacher = TeacherDto(
            id = teacherId,
            firstName = firstName,
            lastName = lastName,
            title = title
        )

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.teacherService.upsertTeacher(teacher)

                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.data != null) {
                        Toast.makeText(this@TeacherFormActivity, 
                            if (isEditMode) getString(R.string.teacher_updated) else getString(R.string.teacher_created), 
                            Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        showError("Failed to save teacher")
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
            errorBody?.contains("TEACHER_NOT_FOUND") == true -> getString(R.string.error_teacher_not_found)
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