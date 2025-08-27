package com.polis.university.data.dto

import com.google.gson.annotations.SerializedName

data class StudentDto(
    val id: Int? = null,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    val email: String,
    val phone: String? = null,
    val course: CourseDto? = null
)

data class CourseDto(
    val id: Int? = null,
    val title: String,
    val code: String,
    val description: String? = null,
    val year: Int? = null
)

data class TeacherDto(
    val id: Int? = null,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    val title: String,
    val courses: List<CourseDto>? = null
)

// API Response DTOs
data class ApiResponse<T>(
    val data: T,
    val status: ApiStatus,
    val error: ApiError? = null
)

data class ApiStatus(
    val code: Int,
    val message: String
)

data class ApiError(
    val code: String,
    val message: String,
    val severity: String
)

data class StudentListResponse(
    val slice: StudentSlice,
    val status: ApiStatus,
    val error: ApiError? = null
)

data class StudentSlice(
    val content: List<StudentDto>,
    val hasNext: Boolean,
    val pageable: Pageable
)

data class Pageable(
    val pageNumber: Int,
    val pageSize: Int
)

// Filter DTOs
data class StudentFilter(
    val filter: String? = null,
    val pagination: Pagination,
    val sorting: Sorting? = null
)

data class Pagination(
    val pageNumber: Int,
    val pageSize: Int
)

data class Sorting(
    val field: String,
    val direction: String // "ASC" or "DESC"
)

// Teacher and Course Filter DTOs
data class TeacherFilter(
    val filter: String? = null,
    val pagination: Pagination,
    val sorting: Sorting? = null
)

data class CourseFilter(
    val filter: String? = null,
    val pagination: Pagination,
    val sorting: Sorting? = null
)
