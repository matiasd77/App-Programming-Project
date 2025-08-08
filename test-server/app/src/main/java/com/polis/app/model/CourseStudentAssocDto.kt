package com.polis.app.model

import com.google.gson.annotations.SerializedName

data class CourseStudentAssocDto(
    @SerializedName("courseId")
    val courseId: Long,
    
    @SerializedName("studentId")
    val studentId: Long
) 