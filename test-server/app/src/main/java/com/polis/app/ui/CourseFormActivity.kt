package com.polis.app.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputLayout
import com.polis.app.R
import com.polis.app.api.RetrofitClient
import com.polis.app.databinding.ActivityCourseFormBinding
import com.polis.app.model.LongIdDto
import com.polis.app.model.CourseDto
import kotlinx.coroutines.launch

class CourseFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCourseFormBinding
    private var courseId: Long? = null
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupForm()
        loadCourseData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = if (isEditMode) getString(R.string.edit_course) else getString(R.string.add_course)
    }

    private fun setupForm() {
        binding.saveButton.setOnClickListener {
            if (validateForm()) {
                saveCourse()
            }
        }
    }

    private fun loadCourseData() {
        courseId = intent.getLongExtra("course_id", -1)
        isEditMode = courseId != -1L

        if (isEditMode) {
            loadExistingCourse()
        } else {
            supportActionBar?.title = getString(R.string.add_course)
        }
    }

    private fun loadExistingCourse() {
        courseId?.let { id ->
            lifecycleScope.launch {
                try {
                    val request = LongIdDto(id = id)
                    val response = RetrofitClient.courseService.getCourse(request)

                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null && result.data != null) {
                            val course = result.data
                            populateForm(course)
                            supportActionBar?.title = getString(R.string.edit_course)
                        } else {
                            showError("Course not found")
                            finish()
                        }
                    } else {
                        showError("Failed to load course: ${response.code()}")
                        finish()
                    }
                } catch (e: Exception) {
                    showError("Network error: ${e.message}")
                    finish()
                }
            }
        }
    }

    private fun populateForm(course: CourseDto) {
        binding.titleInput.editText?.setText(course.title)
        binding.codeInput.editText?.setText(course.code)
        binding.descriptionInput.editText?.setText(course.description)
        binding.yearInput.editText?.setText(course.year?.toString())
    }

    private fun validateForm(): Boolean {
        var isValid = true

        // Validate title
        val title = binding.titleInput.editText?.text?.toString()?.trim()
        if (title.isNullOrEmpty()) {
            binding.titleInput.error = getString(R.string.error_title_required)
            isValid = false
        } else {
            binding.titleInput.error = null
        }

        // Validate code
        val code = binding.codeInput.editText?.text?.toString()?.trim()
        if (code.isNullOrEmpty()) {
            binding.codeInput.error = getString(R.string.error_code_required)
            isValid = false
        } else {
            binding.codeInput.error = null
        }

        // Validate year
        val yearText = binding.yearInput.editText?.text?.toString()?.trim()
        if (yearText.isNullOrEmpty()) {
            binding.yearInput.error = getString(R.string.error_year_required)
            isValid = false
        } else {
            try {
                val year = yearText.toInt()
                if (year < 1900 || year > 2100) {
                    binding.yearInput.error = getString(R.string.error_year_invalid)
                    isValid = false
                } else {
                    binding.yearInput.error = null
                }
            } catch (e: NumberFormatException) {
                binding.yearInput.error = getString(R.string.error_year_invalid)
                isValid = false
            }
        }

        return isValid
    }

    private fun saveCourse() {
        val title = binding.titleInput.editText?.text?.toString()?.trim() ?: ""
        val code = binding.codeInput.editText?.text?.toString()?.trim() ?: ""
        val description = binding.descriptionInput.editText?.text?.toString()?.trim()
        val year = binding.yearInput.editText?.text?.toString()?.trim()?.toIntOrNull()

        val course = CourseDto(
            id = courseId,
            title = title,
            code = code,
            description = description,
            year = year
        )

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.courseService.upsertCourse(course)

                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null && result.data != null) {
                        Toast.makeText(this@CourseFormActivity, 
                            if (isEditMode) getString(R.string.course_updated) else getString(R.string.course_created), 
                            Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        showError("Failed to save course")
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
            errorBody?.contains("COURSE_NOT_FOUND") == true -> getString(R.string.error_course_not_found)
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