package com.polis.app.model

import com.google.gson.annotations.SerializedName

data class CourseDto(
    @SerializedName("id")
    val id: Long? = null,
    
    @SerializedName("code")
    val code: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("year")
    val year: Int,
    
    @SerializedName("teacher")
    val teacher: TeacherDto? = null,
    
    @SerializedName("students")
    val students: List<StudentDto>? = null
) 