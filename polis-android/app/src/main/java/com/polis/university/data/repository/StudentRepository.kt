package com.polis.university.data.repository

import com.polis.university.data.api.ApiService
import com.polis.university.data.api.CourseIdRequest
import com.polis.university.data.api.CourseListResponse
import com.polis.university.data.api.StudentIdRequest
import com.polis.university.data.api.TeacherIdRequest
import com.polis.university.data.api.TeacherListResponse
import com.polis.university.data.dto.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentRepository @Inject constructor(
    private val apiService: ApiService
) {
    
    fun getStudents(filter: StudentFilter = StudentFilter(
        pagination = Pagination(0, 200)
    )): Flow<Result<StudentListResponse>> = flow {
        try {
            val response = apiService.filterStudents(filter)
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun getStudent(id: Int): Flow<Result<StudentDto>> = flow {
        try {
            val response = apiService.getStudent(StudentIdRequest(id))
            emit(Result.success(response.data))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun createStudent(student: StudentDto): Flow<Result<StudentDto>> = flow {
        try {
            val response = apiService.upsertStudent(student)
            emit(Result.success(response.data))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun updateStudent(student: StudentDto): Flow<Result<StudentDto>> = flow {
        try {
            val response = apiService.upsertStudent(student)
            emit(Result.success(response.data))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun deleteStudent(id: Int): Flow<Result<Unit>> = flow {
        try {
            val response = apiService.deleteStudent(id)
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}

// Teacher Repository with full CRUD implementation
@Singleton
class TeacherRepository @Inject constructor(
    private val apiService: ApiService
) {
    
    fun getTeachers(filter: TeacherFilter = TeacherFilter(
        pagination = Pagination(0, 20)
    )): Flow<Result<TeacherListResponse>> = flow {
        try {
            val response = apiService.filterTeachers(filter)
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun getTeacher(id: Int): Flow<Result<TeacherDto>> = flow {
        try {
            val response = apiService.getTeacher(TeacherIdRequest(id))
            emit(Result.success(response.data))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun createTeacher(teacher: TeacherDto): Flow<Result<TeacherDto>> = flow {
        try {
            val response = apiService.upsertTeacher(teacher)
            emit(Result.success(response.data))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun updateTeacher(teacher: TeacherDto): Flow<Result<TeacherDto>> = flow {
        try {
            val response = apiService.upsertTeacher(teacher)
            emit(Result.success(response.data))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun deleteTeacher(id: Int): Flow<Result<Unit>> = flow {
        try {
            val response = apiService.deleteTeacher(id)
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}

// Course Repository with full CRUD implementation
@Singleton
class CourseRepository @Inject constructor(
    private val apiService: ApiService
) {
    
    fun getCourses(filter: CourseFilter = CourseFilter(
        pagination = Pagination(0, 20)
    )): Flow<Result<CourseListResponse>> = flow {
        try {
            val response = apiService.filterCourses(filter)
            emit(Result.success(response))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun getCourse(id: Int): Flow<Result<CourseDto>> = flow {
        try {
            val response = apiService.getCourse(CourseIdRequest(id))
            emit(Result.success(response.data))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun createCourse(course: CourseDto): Flow<Result<CourseDto>> = flow {
        try {
            val response = apiService.upsertCourse(course)
            emit(Result.success(response.data))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun updateCourse(course: CourseDto): Flow<Result<CourseDto>> = flow {
        try {
            val response = apiService.upsertCourse(course)
            emit(Result.success(response.data))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    fun deleteCourse(id: Int): Flow<Result<Unit>> = flow {
        try {
            val response = apiService.deleteCourse(id)
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
