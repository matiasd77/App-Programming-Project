package com.polis.app.model

enum class ServerErrorEnum(val code: String, val message: String) {
    DELETE_STUDENT_NOT_ALLOWED("DELETE_STUDENT_NOT_ALLOWED", "Cannot delete student with associated course"),
    DELETE_TEACHER_NOT_ALLOWED("DELETE_TEACHER_NOT_ALLOWED", "Cannot delete teacher with associated courses"),
    DELETE_COURSE_NOT_ALLOWED("DELETE_COURSE_NOT_ALLOWED", "Cannot delete course with associated students"),
    STUDENT_NOT_FOUND("STUDENT_NOT_FOUND", "Student not found"),
    TEACHER_NOT_FOUND("TEACHER_NOT_FOUND", "Teacher not found"),
    COURSE_NOT_FOUND("COURSE_NOT_FOUND", "Course not found"),
    VALIDATION_ERROR("VALIDATION_ERROR", "Validation error"),
    UNKNOWN_ERROR("UNKNOWN_ERROR", "An unknown error occurred")
} 