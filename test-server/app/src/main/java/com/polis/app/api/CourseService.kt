package com.polis.app.api

import com.polis.app.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CourseService {
    
    @POST("upsertCourse")
    suspend fun upsertCourse(@Body course: CourseDto): Response<RespSingleDto<CourseDto>>
    
    @POST("filterCourses")
    suspend fun filterCourses(@Body filter: SimpleStringFilterDto): Response<RespSliceDto<CourseDto>>
    
    @POST("deleteCourse")
    suspend fun deleteCourse(@Body request: LongIdDto): Response<RespSingleDto<CourseDto>>
    
    @POST("getCourse")
    suspend fun getCourse(@Body request: LongIdDto): Response<RespSingleDto<CourseDto>>
    
    @POST("associateTeacherToCourse")
    suspend fun associateTeacherToCourse(@Body request: CourseTeacherAssocDto): Response<RespSingleDto<CourseDto>>
    
    @POST("removeTeacherFromCourse")
    suspend fun removeTeacherFromCourse(@Body request: CourseTeacherAssocDto): Response<RespSingleDto<CourseDto>>
} 