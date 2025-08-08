package com.polis.app.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.polis.app.R
import com.polis.app.adapter.StudentAdapter
import com.polis.app.api.RetrofitClient
import com.polis.app.databinding.ActivityStudentListBinding
import com.polis.app.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StudentListActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityStudentListBinding
    private lateinit var adapter: StudentAdapter
    private var currentPage = 0
    private var hasNextPage = true
    private var isLoading = false
    private var searchQuery = ""
    private val students = mutableListOf<StudentDto>()
    
    companion object {
        private const val PAGE_SIZE = 20
        private const val SEARCH_DELAY = 500L
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupRecyclerView()
        setupSearchBar()
        setupSwipeRefresh()
        setupFab()
        
        loadStudents(reset = true)
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.students)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    
    private fun setupRecyclerView() {
        adapter = StudentAdapter(
            students = students,
            onEditClick = { student -> openStudentForm(student) },
            onDeleteClick = { student -> showDeleteConfirmation(student) }
        )
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@StudentListActivity)
            adapter = this@StudentListActivity.adapter
        }
        
        // Infinite scroll
        binding.recyclerView.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                
                if (!isLoading && hasNextPage && 
                    (visibleItemCount + firstVisibleItemPosition) >= totalItemCount &&
                    firstVisibleItemPosition >= 0) {
                    loadStudents(reset = false)
                }
            }
        })
    }
    
    private fun setupSearchBar() {
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchQuery = s?.toString() ?: ""
                lifecycleScope.launch {
                    delay(SEARCH_DELAY)
                    loadStudents(reset = true)
                }
            }
        })
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            loadStudents(reset = true)
        }
    }
    
    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            openStudentForm(null)
        }
    }
    
    private fun loadStudents(reset: Boolean) {
        if (isLoading) return
        
        if (reset) {
            currentPage = 0
            hasNextPage = true
            students.clear()
            adapter.notifyDataSetChanged()
        }
        
        if (!hasNextPage) return
        
        isLoading = true
        showLoading(true)
        
        lifecycleScope.launch {
            try {
                val pagination = Pagination(
                    pageNumber = currentPage,
                    pageSize = PAGE_SIZE
                )
                
                val filter = SimpleStringFilterDto(
                    filter = searchQuery,
                    pagination = pagination
                )
                
                val response = RetrofitClient.studentService.filterStudents(filter)
                
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null) {
                        val newStudents = result.data ?: emptyList()
                        students.addAll(newStudents)
                        adapter.notifyDataSetChanged()
                        
                        hasNextPage = result.hasNext
                        currentPage++
                        
                        if (students.isEmpty()) {
                            showEmptyState(true)
                        } else {
                            showEmptyState(false)
                        }
                    }
                } else {
                    showError("Failed to load students: ${response.code()}")
                }
            } catch (e: Exception) {
                showError("Network error: ${e.message}")
            } finally {
                isLoading = false
                showLoading(false)
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }
    
    private fun openStudentForm(student: StudentDto?) {
        val intent = Intent(this, StudentFormActivity::class.java)
        if (student != null) {
            intent.putExtra("student_id", student.id)
        }
        startActivity(intent)
    }
    
    private fun showDeleteConfirmation(student: StudentDto) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_student))
            .setMessage(getString(R.string.delete_student_confirmation))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                deleteStudent(student)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
    
    private fun deleteStudent(student: StudentDto) {
        student.id?.let { studentId ->
            lifecycleScope.launch {
                try {
                    val request = LongIdDto(id = studentId)
                    val response = RetrofitClient.studentService.deleteStudent(request)
                    
                    if (response.isSuccessful) {
                        Toast.makeText(this@StudentListActivity, getString(R.string.student_deleted), Toast.LENGTH_SHORT).show()
                        loadStudents(reset = true)
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
    }
    
    private fun parseErrorMessage(errorBody: String?): String {
        return when {
            errorBody?.contains("DELETE_STUDENT_NOT_ALLOWED") == true -> getString(R.string.error_delete_student_not_allowed)
            errorBody?.contains("STUDENT_NOT_FOUND") == true -> getString(R.string.error_student_not_found)
            errorBody?.contains("VALIDATION_ERROR") == true -> getString(R.string.error_validation)
            else -> getString(R.string.error_unknown)
        }
    }
    
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
    
    private fun showEmptyState(show: Boolean) {
        binding.emptyState.visibility = if (show) View.VISIBLE else View.GONE
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    
    override fun onResume() {
        super.onResume()
        // Refresh the list when returning from form
        loadStudents(reset = true)
    }
} 