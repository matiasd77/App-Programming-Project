package com.polis.app.model

import com.google.gson.annotations.SerializedName

data class SimpleStringFilterDto(
    @SerializedName("filter")
    val filter: String,
    
    @SerializedName("pagination")
    val pagination: Pagination
) 