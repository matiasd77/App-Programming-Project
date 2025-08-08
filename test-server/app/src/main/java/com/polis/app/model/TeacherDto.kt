package com.polis.app.model

import com.google.gson.annotations.SerializedName

data class TeacherDto(
    @SerializedName("id")
    val id: Long? = null,
    
    @SerializedName("firstName")
    val firstName: String,
    
    @SerializedName("lastName")
    val lastName: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("courses")
    val courses: List<CourseDto>? = null
) 