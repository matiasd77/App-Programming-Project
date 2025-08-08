package com.polis.app.api

import com.polis.app.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TeacherService {
    
    @POST("upsertTeacher")
    suspend fun upsertTeacher(@Body teacher: TeacherDto): Response<RespSingleDto<TeacherDto>>
    
    @POST("filterTeachers")
    suspend fun filterTeachers(@Body filter: SimpleStringFilterDto): Response<RespSliceDto<TeacherDto>>
    
    @POST("deleteTeacher")
    suspend fun deleteTeacher(@Body request: LongIdDto): Response<RespSingleDto<TeacherDto>>
    
    @POST("getTeacher")
    suspend fun getTeacher(@Body request: LongIdDto): Response<RespSingleDto<TeacherDto>>
} 