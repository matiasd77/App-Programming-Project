package com.polis.app.model

import com.google.gson.annotations.SerializedName

data class ResponseWithStatusDto<T>(
    @SerializedName("data")
    val data: T? = null,
    
    @SerializedName("status")
    val status: List<ServerStatus>? = null
) 