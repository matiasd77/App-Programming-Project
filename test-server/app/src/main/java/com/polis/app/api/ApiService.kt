package com.polis.app.api

import com.polis.app.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    companion object {
        const val BASE_URL = "http://10.0.2.2:8080/"
    }
} 