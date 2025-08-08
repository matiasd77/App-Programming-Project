package com.polis.app.model

import com.google.gson.annotations.SerializedName

data class Pagination(
    @SerializedName("pageNumber")
    val pageNumber: Int,
    
    @SerializedName("pageSize")
    val pageSize: Int,
    
    @SerializedName("sort")
    val sort: Sorting? = null
) 