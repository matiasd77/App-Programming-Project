package com.polis.app.model

import com.google.gson.annotations.SerializedName

data class RespSliceDto<T>(
    @SerializedName("data")
    val data: List<T>? = null,
    
    @SerializedName("status")
    val status: List<ServerStatus>? = null,
    
    @SerializedName("hasNext")
    val hasNext: Boolean = false,
    
    @SerializedName("pageNumber")
    val pageNumber: Int = 0,
    
    @SerializedName("pageSize")
    val pageSize: Int = 0
) 