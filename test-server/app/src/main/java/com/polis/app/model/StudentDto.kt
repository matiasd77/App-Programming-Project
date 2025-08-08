package com.polis.app.model

import com.google.gson.annotations.SerializedName

data class StudentDto(
    @SerializedName("id")
    val id: Long? = null,
    
    @SerializedName("firstName")
    val firstName: String,
    
    @SerializedName("lastName")
    val lastName: String,
    
    @SerializedName("serialNumber")
    val serialNumber: String,
    
    @SerializedName("course")
    val course: CourseDto? = null
) 