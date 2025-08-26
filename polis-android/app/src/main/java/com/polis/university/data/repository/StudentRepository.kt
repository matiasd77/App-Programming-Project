package com.polis.university.data.repository

import com.polis.university.data.api.ApiService
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
        pagination = Pagination(0, 20)
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

// Placeholder repositories for future implementation
@Singleton
class TeacherRepository @Inject constructor(
    private val apiService: ApiService
) {
    // TODO: Implement teacher CRUD operations
}

@Singleton
class CourseRepository @Inject constructor(
    private val apiService: ApiService
) {
    // TODO: Implement course CRUD operations
}
