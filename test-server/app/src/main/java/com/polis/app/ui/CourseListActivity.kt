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
import com.polis.app.adapter.CourseAdapter
import com.polis.app.api.RetrofitClient
import com.polis.app.databinding.ActivityCourseListBinding
import com.polis.app.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CourseListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCourseListBinding
    private lateinit var adapter: CourseAdapter
    private var currentPage = 0
    private var hasNextPage = true
    private var isLoading = false
    private var searchQuery = ""
    private val courses = mutableListOf<CourseDto>()

    companion object {
        private const val PAGE_SIZE = 20
        private const val SEARCH_DELAY = 500L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCourseListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupSearchBar()
        setupSwipeRefresh()
        setupFab()

        loadCourses(reset = true)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.courses)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupRecyclerView() {
        adapter = CourseAdapter(
            courses = courses,
            onEditClick = { course -> openCourseForm(course) },
            onDeleteClick = { course -> showDeleteConfirmation(course) }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@CourseListActivity)
            adapter = this@CourseListActivity.adapter
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
                    loadCourses(reset = false)
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
                    loadCourses(reset = true)
                }
            }
        })
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            loadCourses(reset = true)
        }
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            openCourseForm(null)
        }
    }

    private fun loadCourses(reset: Boolean) {
        if (isLoading) return

        if (reset) {
            currentPage = 0
            hasNextPage = true
            courses.clear()
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

                val response = RetrofitClient.courseService.filterCourses(filter)

                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null) {
                        val newCourses = result.data ?: emptyList()
                        courses.addAll(newCourses)
                        adapter.notifyDataSetChanged()

                        hasNextPage = result.hasNext
                        currentPage++

                        if (courses.isEmpty()) {
                            showEmptyState(true)
                        } else {
                            showEmptyState(false)
                        }
                    }
                } else {
                    showError("Failed to load courses: ${response.code()}")
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

    private fun openCourseForm(course: CourseDto?) {
        val intent = Intent(this, CourseFormActivity::class.java)
        if (course != null) {
            intent.putExtra("course_id", course.id)
        }
        startActivity(intent)
    }

    private fun showDeleteConfirmation(course: CourseDto) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_course))
            .setMessage(getString(R.string.delete_course_confirmation))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                deleteCourse(course)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun deleteCourse(course: CourseDto) {
        course.id?.let { courseId ->
            lifecycleScope.launch {
                try {
                    val request = LongIdDto(id = courseId)
                    val response = RetrofitClient.courseService.deleteCourse(request)

                    if (response.isSuccessful) {
                        Toast.makeText(this@CourseListActivity, getString(R.string.course_deleted), Toast.LENGTH_SHORT).show()
                        loadCourses(reset = true)
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
            errorBody?.contains("DELETE_COURSE_NOT_ALLOWED") == true -> getString(R.string.error_delete_course_not_allowed)
            errorBody?.contains("COURSE_NOT_FOUND") == true -> getString(R.string.error_course_not_found)
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
        loadCourses(reset = true)
    }
} 