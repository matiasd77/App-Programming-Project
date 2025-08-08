package com.polis.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.polis.app.databinding.ItemTeacherBinding
import com.polis.app.model.TeacherDto

class TeacherAdapter(
    private val teachers: List<TeacherDto>,
    private val onEditClick: (TeacherDto) -> Unit,
    private val onDeleteClick: (TeacherDto) -> Unit
) : RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder>() {

    inner class TeacherViewHolder(private val binding: ItemTeacherBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(teacher: TeacherDto) {
            binding.apply {
                teacherName.text = "${teacher.firstName} ${teacher.lastName}"
                teacherTitle.text = teacher.title ?: "No title"
                
                // Show course count if available
                val courseCount = teacher.courses?.size ?: 0
                teacherCourses.text = "$courseCount course${if (courseCount != 1) "s" else ""}"

                editButton.setOnClickListener {
                    onEditClick(teacher)
                }

                deleteButton.setOnClickListener {
                    onDeleteClick(teacher)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherViewHolder {
        val binding = ItemTeacherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TeacherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TeacherViewHolder, position: Int) {
        holder.bind(teachers[position])
    }

    override fun getItemCount(): Int = teachers.size
} 