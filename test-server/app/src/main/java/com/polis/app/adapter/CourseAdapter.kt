package com.polis.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.polis.app.databinding.ItemCourseBinding
import com.polis.app.model.CourseDto

class CourseAdapter(
    private val courses: List<CourseDto>,
    private val onEditClick: (CourseDto) -> Unit,
    private val onDeleteClick: (CourseDto) -> Unit
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    inner class CourseViewHolder(private val binding: ItemCourseBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(course: CourseDto) {
            binding.apply {
                courseTitle.text = course.title
                courseCode.text = course.code
                courseDescription.text = course.description ?: "No description"
                courseYear.text = "Year: ${course.year}"
                
                // Show teacher name if available
                val teacherName = course.teacher?.let { "${it.firstName} ${it.lastName}" } ?: "No teacher assigned"
                courseTeacher.text = "Teacher: $teacherName"
                
                // Show student count if available
                val studentCount = course.students?.size ?: 0
                courseStudents.text = "$studentCount student${if (studentCount != 1) "s" else ""}"

                editButton.setOnClickListener {
                    onEditClick(course)
                }

                deleteButton.setOnClickListener {
                    onDeleteClick(course)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = ItemCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(courses[position])
    }

    override fun getItemCount(): Int = courses.size
} 