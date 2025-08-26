package com.polis.university.data.api

import com.polis.university.data.dto.*
import retrofit2.http.*

interface ApiService {
    
    // Student endpoints
    @POST("student/filter")
    suspend fun filterStudents(@Body filter: StudentFilter): StudentListResponse
    
    @POST("student/get")
    suspend fun getStudent(@Body request: StudentIdRequest): ApiResponse<StudentDto>
    
    @POST("student/upsert")
    suspend fun upsertStudent(@Body student: StudentDto): ApiResponse<StudentDto>
    
    @DELETE("student/{id}")
    suspend fun deleteStudent(@Path("id") id: Int): ApiResponse<Unit>
    
    // Teacher endpoints (placeholders for future implementation)
    @POST("teacher/filter")
    suspend fun filterTeachers(@Body filter: StudentFilter): TeacherListResponse
    
    @POST("teacher/get")
    suspend fun getTeacher(@Body request: StudentIdRequest): ApiResponse<TeacherDto>
    
    @POST("teacher/upsert")
    suspend fun upsertTeacher(@Body teacher: TeacherDto): ApiResponse<TeacherDto>
    
    @DELETE("teacher/{id}")
    suspend fun deleteTeacher(@Path("id") id: Int): ApiResponse<Unit>
    
    // Course endpoints (placeholders for future implementation)
    @POST("course/filter")
    suspend fun filterCourses(@Body filter: StudentFilter): CourseListResponse
    
    @POST("course/get")
    suspend fun getCourse(@Body request: StudentIdRequest): ApiResponse<CourseDto>
    
    @POST("course/upsert")
    suspend fun upsertCourse(@Body course: CourseDto): ApiResponse<CourseDto>
    
    @DELETE("course/{id}")
    suspend fun deleteCourse(@Path("id") id: Int): ApiResponse<Unit>
}

// Request DTOs
data class StudentIdRequest(val id: Int)

// Response DTOs for Teachers and Courses (placeholders)
data class TeacherListResponse(
    val slice: TeacherSlice,
    val status: ApiStatus,
    val error: ApiError? = null
)

data class TeacherSlice(
    val content: List<TeacherDto>,
    val hasNext: Boolean,
    val pageable: Pageable
)

data class CourseListResponse(
    val slice: CourseSlice,
    val status: ApiStatus,
    val error: ApiError? = null
)

data class CourseSlice(
    val content: List<CourseDto>,
    val hasNext: Boolean,
    val pageable: Pageable
)
