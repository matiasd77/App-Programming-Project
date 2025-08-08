package com.polis.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.polis.app.databinding.ItemStudentBinding
import com.polis.app.model.StudentDto

class StudentAdapter(
    private val students: List<StudentDto>,
    private val onEditClick: (StudentDto) -> Unit,
    private val onDeleteClick: (StudentDto) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(students[position])
    }

    override fun getItemCount(): Int = students.size

    inner class StudentViewHolder(
        private val binding: ItemStudentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(student: StudentDto) {
            binding.apply {
                studentName.text = "${student.firstName} ${student.lastName}"
                studentSerialNumber.text = student.serialNumber
                studentCourse.text = student.course?.title ?: "No course assigned"
                
                btnEdit.setOnClickListener { onEditClick(student) }
                btnDelete.setOnClickListener { onDeleteClick(student) }
            }
        }
    }
} 