package com.polis.app.api

import com.polis.app.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface StudentService {
    
    @POST("upsertStudent")
    suspend fun upsertStudent(@Body student: StudentDto): Response<RespSingleDto<StudentDto>>
    
    @POST("filterStudents")
    suspend fun filterStudents(@Body filter: SimpleStringFilterDto): Response<RespSliceDto<StudentDto>>
    
    @POST("deleteStudent")
    suspend fun deleteStudent(@Body request: LongIdDto): Response<RespSingleDto<StudentDto>>
    
    @POST("getStudent")
    suspend fun getStudent(@Body request: LongIdDto): Response<RespSingleDto<StudentDto>>
    
    @POST("associateStudentToCourse")
    suspend fun associateStudentToCourse(@Body request: CourseStudentAssocDto): Response<RespSingleDto<StudentDto>>
    
    @POST("removeStudentFromCourse")
    suspend fun removeStudentFromCourse(@Body request: CourseStudentAssocDto): Response<RespSingleDto<StudentDto>>
} 