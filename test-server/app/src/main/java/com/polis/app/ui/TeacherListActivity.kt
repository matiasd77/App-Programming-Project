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
import com.polis.app.adapter.TeacherAdapter
import com.polis.app.api.RetrofitClient
import com.polis.app.databinding.ActivityTeacherListBinding
import com.polis.app.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TeacherListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherListBinding
    private lateinit var adapter: TeacherAdapter
    private var currentPage = 0
    private var hasNextPage = true
    private var isLoading = false
    private var searchQuery = ""
    private val teachers = mutableListOf<TeacherDto>()

    companion object {
        private const val PAGE_SIZE = 20
        private const val SEARCH_DELAY = 500L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupSearchBar()
        setupSwipeRefresh()
        setupFab()

        loadTeachers(reset = true)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.teachers)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRecyclerView() {
        adapter = TeacherAdapter(
            teachers = teachers,
            onEditClick = { teacher -> openTeacherForm(teacher) },
            onDeleteClick = { teacher -> showDeleteConfirmation(teacher) }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@TeacherListActivity)
            adapter = this@TeacherListActivity.adapter
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
                    loadTeachers(reset = false)
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
                    loadTeachers(reset = true)
                }
            }
        })
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            loadTeachers(reset = true)
        }
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            openTeacherForm(null)
        }
    }

    private fun loadTeachers(reset: Boolean) {
        if (isLoading) return

        if (reset) {
            currentPage = 0
            hasNextPage = true
            teachers.clear()
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

                val response = RetrofitClient.teacherService.filterTeachers(filter)

                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null) {
                        val newTeachers = result.data ?: emptyList()
                        teachers.addAll(newTeachers)
                        adapter.notifyDataSetChanged()

                        hasNextPage = result.hasNext
                        currentPage++

                        if (teachers.isEmpty()) {
                            showEmptyState(true)
                        } else {
                            showEmptyState(false)
                        }
                    }
                } else {
                    showError("Failed to load teachers: ${response.code()}")
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

    private fun openTeacherForm(teacher: TeacherDto?) {
        val intent = Intent(this, TeacherFormActivity::class.java)
        if (teacher != null) {
            intent.putExtra("teacher_id", teacher.id)
        }
        startActivity(intent)
    }

    private fun showDeleteConfirmation(teacher: TeacherDto) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_teacher))
            .setMessage(getString(R.string.delete_teacher_confirmation))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                deleteTeacher(teacher)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun deleteTeacher(teacher: TeacherDto) {
        teacher.id?.let { teacherId ->
            lifecycleScope.launch {
                try {
                    val request = LongIdDto(id = teacherId)
                    val response = RetrofitClient.teacherService.deleteTeacher(request)

                    if (response.isSuccessful) {
                        Toast.makeText(this@TeacherListActivity, getString(R.string.teacher_deleted), Toast.LENGTH_SHORT).show()
                        loadTeachers(reset = true)
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
            errorBody?.contains("DELETE_TEACHER_NOT_ALLOWED") == true -> getString(R.string.error_delete_teacher_not_allowed)
            errorBody?.contains("TEACHER_NOT_FOUND") == true -> getString(R.string.error_teacher_not_found)
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
        loadTeachers(reset = true)
    }
} 