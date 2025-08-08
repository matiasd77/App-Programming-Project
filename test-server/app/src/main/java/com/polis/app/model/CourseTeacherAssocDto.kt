package com.polis.app.model

import com.google.gson.annotations.SerializedName

data class CourseTeacherAssocDto(
    @SerializedName("courseId")
    val courseId: Long,
    
    @SerializedName("teacherId")
    val teacherId: Long
) 